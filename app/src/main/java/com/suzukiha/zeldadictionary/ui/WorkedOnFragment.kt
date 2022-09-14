package com.suzukiha.zeldadictionary.ui

import android.app.Dialog
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.suzukiha.zeldaapiclient.firestore.ZeldaFirestoreFunctions
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.databinding.FragmentWorkedOnSheetBinding
import com.suzukiha.zeldadictionary.model.Game
import com.suzukiha.zeldadictionary.util.SpringItemAnimator
import com.suzukiha.zeldadictionary.viewmodel.WorkedOnViewModel
import kotlinx.coroutines.launch

class WorkedOnFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentWorkedOnSheetBinding? = null
    private val binding get() = _binding!!

    private val args: WorkedOnFragmentArgs by navArgs()

    private val viewModel: WorkedOnViewModel by viewModels()

    private val workedOnGameListAdapter: WorkedOnGameListAdapter by lazy {
        WorkedOnGameListAdapter()
    }
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun getTheme(): Int {
        return R.style.StaffBottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(
            requireContext(),
            theme
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        _binding = FragmentWorkedOnSheetBinding.inflate(inflater, container, false).apply {
            this.staff.text = args.staff.name
            this.workedOnList.apply {
                itemAnimator = SpringItemAnimator()
                adapter = workedOnGameListAdapter
                layoutManager = linearLayoutManager
                addItemDecoration(
                    InsetDivider(
                        resources.getDimensionPixelSize(R.dimen.divider_insets),
                        resources.getDimensionPixelSize((R.dimen.divider_insets)),
                        context.getColor(R.color.divider)
                    )
                )
            }
            this.collapse.setOnClickListener {
                dismiss()
            }
        }
        dialog?.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                BottomSheetBehavior.from(bottomSheet).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    peekHeight = resources.getDimensionPixelSize(R.dimen.peek_height)
                }
            }
        }
        viewModel.fetchGamesFromFirestore(args.staff.workedOnGameId)
        subscribeData()
        return binding.root
    }

    private fun subscribeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.workedOnGameFromFirestore.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    when (it) {
                        is ZeldaFirestoreFunctions.GamesState.Loading -> {
                        }
                        is ZeldaFirestoreFunctions.GamesState.Success -> {
                            val currentList = workedOnGameListAdapter.currentList.toMutableList()
                            val itemList = it.gameList.map { game ->
                                Game(
                                    id = game.id.toInt(),
                                    name = viewModel.getTextFromUseLanguage(text = game.name),
                                    thumbnailUrl = game.thumbnailUrl,
                                    description = viewModel.getTextFromUseLanguage(text = game.description),
                                    releaseDate = game.releaseDate
                                )
                            }
                            currentList.addAll(itemList)
                            workedOnGameListAdapter.submitList(currentList)
                        }
                        is ZeldaFirestoreFunctions.GamesState.Error -> {
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.collapse.setOnClickListener(null)
        _binding = null
    }
}

class InsetDivider(
    @Px private val inset: Int,
    @Px private val height: Int,
    @ColorInt private val dividerColor: Int
) : RecyclerView.ItemDecoration() {

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = dividerColor
        style = Paint.Style.STROKE
        strokeWidth = height.toFloat()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val points = mutableListOf<Float>()
        parent.forEach {
            if (parent.getChildAdapterPosition(it) < state.itemCount - 1) {
                val bottom = it.bottom.toFloat()
                points.add((it.left + inset).toFloat())
                points.add(bottom)
                points.add(it.right.toFloat())
                points.add(bottom)
            }
        }
        c.drawLines(points.toFloatArray(), dividerPaint)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = height / 2
        outRect.bottom = height / 2
    }
}
