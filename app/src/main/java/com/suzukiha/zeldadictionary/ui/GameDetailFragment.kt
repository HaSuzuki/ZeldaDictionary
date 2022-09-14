package com.suzukiha.zeldadictionary.ui

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.suzukiha.zeldaapiclient.firestore.ZeldaFirestoreFunctions
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.databinding.FragmentGameDetailBinding
import com.suzukiha.zeldadictionary.model.Staff
import com.suzukiha.zeldadictionary.util.*
import com.suzukiha.zeldadictionary.viewmodel.GameDetailViewModel
import java.util.concurrent.TimeUnit

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
            this.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            this.list.apply {
                itemAnimator = SpringItemAnimator()
                addItemDecoration(EndSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_12dp)))
                adapter = gameDetailAdapter
                layoutManager = linearLayoutManager
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
        binding.toolbar.updateToolbarInsets()
        postponeEnterTransition(DURATION_SHORT, TimeUnit.MILLISECONDS)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchStaffsFromFirestore(gameId = args.gameId)

        binding.name.let {
            it.text = args.name
            it.contentDescription = args.name
            if (isEnglish()) {
                it.typeface = ResourcesCompat.getFont(it.context, R.font.hylia_serif_beta_regular)
            }
        }
        binding.description.let {
            it.text = args.description
            it.contentDescription = args.description
            if (isEnglish()) {
                it.typeface = ResourcesCompat.getFont(it.context, R.font.hylia_serif_beta_regular)
            }
        }
        args.thumbnailUrl?.let {
            binding.image.load(it) {
                crossfade(true)
                error(R.drawable.dictionary)
                bitmapConfig(Bitmap.Config.ARGB_8888)
                allowHardware(false)
            }
            binding.image.contentDescription = "${args.name} image"
        }
        subscribeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.toolbar.setNavigationOnClickListener(null)
        _binding = null
    }

    private fun subscribeData() {
        viewModel.staffsFromFirestore.observe(
            viewLifecycleOwner,
            Observer {
                if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    return@Observer
                }
                when (it) {
                    is ZeldaFirestoreFunctions.StaffState.Loading -> {
                    }
                    is ZeldaFirestoreFunctions.StaffState.Success -> {
                        val list = arrayListOf<Staff>()
                        it.staff.forEach { staff ->
                            list.add(
                                Staff(
                                    id = staff.id.toInt(),
                                    name = viewModel.getTextFromUseLanguage(staff.name),
                                    workedOnGameId = staff.workedOnGameId
                                )
                            )
                        }
                        gameDetailAdapter.submitList(list)
                        startPostponedEnterTransition()
                    }
                    is ZeldaFirestoreFunctions.StaffState.Error -> {
                    }
                }
            }
        )
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
