package com.suzukiha.zeldadictionary.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suzukiha.zeldaapiclient.firestore.ZeldaFirestoreFunctions
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.databinding.FragmentGameDetailBinding
import com.suzukiha.zeldadictionary.model.Staff
import com.suzukiha.zeldadictionary.util.*
import com.suzukiha.zeldadictionary.viewmodel.GameDetailViewModel
import com.suzukiha.zeldadictionary.ui.GameDetailContent
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class GameDetailFragment : Fragment() {

    private var _binding: FragmentGameDetailBinding? = null
    private val binding get() = _binding!!

    private val args: GameDetailFragmentArgs by navArgs()

    private val viewModel: GameDetailViewModel by viewModels()

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val gameDetailAdapter by lazy {
        GameDetailAdapter itemClick@{ view, staff ->
            val extras = FragmentNavigatorExtras(
                view to requireContext().getString(R.string.default_transition_name)
            )
            val action = GameDetailFragmentDirections.detailToStaffWorkedOnList(staff = staff)
            findNavController().navigate(action, extras)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        _binding = FragmentGameDetailBinding.inflate(inflater, container, false).apply {
            this.list.apply {
                itemAnimator = SpringItemAnimator()
                addItemDecoration(EndSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_12dp)))
                adapter = gameDetailAdapter
                layoutManager = linearLayoutManager
            }
        }

        binding.detailContent.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                MaterialTheme {
                    GameDetailContent(
                        gameId = args.gameId,
                        gameName = args.name,
                        gameDescription = args.description,
                        thumbnailUrl = args.thumbnailUrl,
                        navigation = findNavController()
                    )
                }
            }
        }

        val linearInterpolator = AnimationUtils.loadInterpolator(
            context,
            android.R.interpolator.linear
        )

        sharedElementEnterTransition = MaterialContainerTransition(binding.scroll.id).apply {
            duration = DURATION_NORMAL
            interpolator = linearInterpolator
        }

        sharedElementReturnTransition = MaterialContainerTransition().apply {
            duration = DURATION_TINY
            interpolator = linearInterpolator
        }
        postponeEnterTransition(DURATION_SHORT, TimeUnit.MILLISECONDS)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchStaffsFromFirestore(gameId = args.gameId)
        subscribeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeData() {
        viewModel.staffsFromFirestore.observe(viewLifecycleOwner, Observer {
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                return@Observer
            }
            when (it) {
                is ZeldaFirestoreFunctions.StaffState.Loading -> {

                }
                is ZeldaFirestoreFunctions.StaffState.Success -> {
                    val list = arrayListOf<Staff>()
                    it.staff.forEach { staff ->
                        list.add(Staff(
                            id = staff.id.toInt(),
                            name = viewModel.getTextFromUseLanguage(staff.name),
                            workedOnGameId = staff.workedOnGameId
                        ))
                    }
                    gameDetailAdapter.submitList(list)
                    startPostponedEnterTransition()
                }
                is ZeldaFirestoreFunctions.StaffState.Error -> {

                }
            }
        })
    }
}

class EndSpaceItemDecoration(
    private val spacing: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val lastItem = parent.getChildAdapterPosition(view) == state.itemCount - 1
        val firstItem = parent.getChildAdapterPosition(view) == 0

        outRect.right = if (!lastItem) {
            spacing
        } else {
            0
        }
        outRect.left = if (firstItem) {
            spacing
        } else {
            0
        }
    }
}