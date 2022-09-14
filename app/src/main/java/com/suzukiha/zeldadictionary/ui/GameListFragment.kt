package com.suzukiha.zeldadictionary.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.suzukiha.zeldaapiclient.firestore.ZeldaFirestoreFunctions
import com.suzukiha.zeldaapiclient.retrofit.ZeldaFunctions
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.databinding.FragmentGameListBinding
import com.suzukiha.zeldadictionary.model.Game
import com.suzukiha.zeldadictionary.util.DURATION_SHORT
import com.suzukiha.zeldadictionary.util.SpringItemAnimator
import com.suzukiha.zeldadictionary.util.updateAppbarInsets
import com.suzukiha.zeldadictionary.viewmodel.GameListViewModel
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class GameListFragment : Fragment() {

    private var _binding: FragmentGameListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameListViewModel by viewModels()

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val gameListAdapter by lazy {
        GameListAdapter itemClick@{ view, gameId, name, description, thumbnailUrl ->
            val extras = FragmentNavigatorExtras(
                view to requireContext().getString(R.string.default_transition_name)
            )
            val action = GameListFragmentDirections.actionGameListToGameDetail(gameId, name, description, thumbnailUrl)
            findNavController().navigate(action, extras)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        _binding = FragmentGameListBinding.inflate(inflater, container, false).apply {
            this.list.apply {
                itemAnimator = SpringItemAnimator()
                addItemDecoration(BottomSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.grid)))
                adapter = gameListAdapter
                layoutManager = linearLayoutManager
            }
        }
        if (gameListAdapter.currentList.isNullOrEmpty()) {
            showShimmer()
        }
        binding.toolbar.updateAppbarInsets()
        postponeEnterTransition(DURATION_SHORT, TimeUnit.MILLISECONDS)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.fetchGamesFromApi(page = viewModel.currentPage())
        viewModel.fetchGamesFromFirestore()
        binding.swipeRefreshLayout.let {
            it.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.primary),
                ContextCompat.getColor(requireContext(), R.color.secondary)
            )
            it.setOnRefreshListener {
                viewModel.setCurrentPage(page = "0")
                viewModel.isLastPage = false
//                viewModel.fetchGamesFromApi(
//                    page = viewModel.currentPage()
//                )
                viewModel.fetchGamesFromFirestore()
            }
        }

        binding.list.addOnScrollListener(object : PagingScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                viewModel.isLoading = true
//                viewModel.fetchGamesFromApi(
//                    page = viewModel.currentPage()
//                )
                viewModel.fetchGamesFromFirestore()
            }

            override fun isLastPage(): Boolean {
                return viewModel.isLastPage
            }

            override fun isLoadingPage(): Boolean {
                return viewModel.isLoading
            }
        })
        subscribeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.swipeRefreshLayout.setOnRefreshListener(null)
        _binding = null
    }

    private fun hideShimmer() {
        if (_binding == null) {
            return
        }
        if (binding.shimmerFrameLayout.isShimmerStarted) {
            binding.shimmerFrameLayout.stopShimmer()
            binding.shimmerFrameLayout.isGone = true
        }
    }

    private fun showShimmer() {
        if (_binding == null) {
            return
        }
        if (!binding.shimmerFrameLayout.isShimmerStarted) {
            binding.shimmerFrameLayout.startShimmer()
            binding.shimmerFrameLayout.isVisible = true
        }
    }

    private fun refreshed() {
        if (_binding == null) {
            return
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun subscribeData() {
        // retrofit
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(Locale.getDefault().language)
            .build()
        val translator = Translation.getClient(options)
        lifecycle.addObserver(translator)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.games.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    when (it) {
                        is ZeldaFunctions.GamesState.Loading -> {
                            viewModel.isLoading = true
                        }
                        is ZeldaFunctions.GamesState.Success -> {
                            viewModel.isLoading = false
                            val itemList = gameListAdapter.currentList.toMutableList()
                            if (binding.swipeRefreshLayout.isRefreshing) {
                                itemList.clear()
                            }
                            it.game.data?.let { list ->
                                if (list.isNullOrEmpty()) {
                                    hideShimmer()
                                    viewModel.isLastPage = true
                                    return@let
                                }
                                viewModel.setIncrementPage(page = it.currentPage)
                                list.forEachIndexed { index, gameData ->
                                    gameData.name?.let { name ->
                                        itemList.add(
                                            Game(
                                                id = index,
                                                name = viewModel.translateText(
                                                    text = name,
                                                    translator = translator
                                                ),
                                                thumbnailUrl = null,
                                                description = viewModel.translateText(
                                                    text = gameData.description,
                                                    translator = translator
                                                ),
                                                releaseDate = gameData.releasedDate
                                            )
                                        )
                                    }
                                }
                                hideShimmer()
                                gameListAdapter.submitList(itemList)
                                refreshed()
                                if (it.currentPage == GameListViewModel.DEFAULT_PAGE) {
                                    startPostponedEnterTransition()
                                }
                            } ?: run {
                                hideShimmer()
                                viewModel.isLastPage = true
                                viewModel.isLoading = false
                            }
                        }
                        is ZeldaFunctions.GamesState.Error -> {
                            hideShimmer()
                            viewModel.isLastPage = true
                            viewModel.isLoading = false
                            if (binding.swipeRefreshLayout.isRefreshing) {
                                binding.swipeRefreshLayout.isRefreshing = false
                            }
                        }
                    }
                }
        }

        // firestore
        viewModel.gamesFromFirestore.observe(viewLifecycleOwner) {
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                return@observe
            }
            when (it) {
                is ZeldaFirestoreFunctions.GamesState.Loading -> {
                    viewModel.isLoading = true
                }
                is ZeldaFirestoreFunctions.GamesState.Success -> {
                    val itemList = it.gameList.map { game ->
                        Game(
                            id = game.id.toInt(),
                            name = viewModel.getTextFromUseLanguage(text = game.name),
                            thumbnailUrl = game.thumbnailUrl,
                            description = viewModel.getTextFromUseLanguage(text = game.description),
                            releaseDate = game.releaseDate
                        )
                    }
                    viewModel.isLoading = false
                    hideShimmer()
                    if (gameListAdapter.currentList.isNullOrEmpty()) {
                        startPostponedEnterTransition()
                    }
                    gameListAdapter.submitList(itemList)
                    refreshed()
                }
                is ZeldaFirestoreFunctions.GamesState.Error -> {
                    viewModel.isLoading = false
                }
            }
        }
    }
}

class BottomSpaceItemDecoration(
    private val spacing: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val lastItem = parent.getChildAdapterPosition(view) == state.itemCount - 1
        outRect.bottom = if (!lastItem) {
            spacing
        } else {
            0
        }
    }
}
