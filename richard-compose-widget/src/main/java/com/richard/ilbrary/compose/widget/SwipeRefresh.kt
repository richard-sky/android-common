package com.richard.ilbrary.compose.widget

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.king.ultraswiperefresh.NestedScrollMode
import com.king.ultraswiperefresh.SecondaryBehavior
import com.king.ultraswiperefresh.UltraSwipeRefresh
import com.king.ultraswiperefresh.UltraSwipeRefreshState
import com.king.ultraswiperefresh.rememberUltraSwipeRefreshState
import com.king.ultraswiperefresh.theme.UltraSwipeRefreshTheme
import com.richard.library.context.util.isNotNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


/**
 * 带下拉刷新和上拉加载的列表控件(支持列表和非列表控件)
 * 非列表注意: 非列表需在内容的根节点设置Modifier.verticalScroll(state = rememberScrollState())
 *
 * @param state 状态对象，用于控制和观察 [UltraSwipeRefresh] 的状态，如下拉刷新和上拉加载的触发与控制
 * @param onRefresh 下拉刷新手势触发完成时的回调
 * @param onLoadMore 上拉加载手势触发完成时的回调
 * @param modifier 修饰符，用于装饰或扩展 Compose UI 元素的行为；详细说明见 [Modifier]
 * @param isList 内容是否属于列表控件
 * @param headerScrollMode 下拉刷新时 Header 的滑动模式；详细说明见 [NestedScrollMode]
 * @param footerScrollMode 上拉加载时 Footer 的滑动模式；详细说明见 [NestedScrollMode]
 * @param refreshTriggerRate 触发下拉刷新的最小滑动比例，基于 [headerIndicator] 的高度；默认值：1
 * @param loadMoreTriggerRate 触发上拉加载的最小滑动比例，基于 [footerIndicator] 的高度；默认值：1
 * @param headerSecondaryEnabled 是否启用 Header 二级内容功能
 * @param footerSecondaryEnabled 是否启用 Footer 二级内容功能
 * @param headerSecondaryBehavior Header 二级内容交互行为模式；详细说明见 [SecondaryBehavior]
 * @param footerSecondaryBehavior Footer 二级内容交互行为模式；详细说明见 [SecondaryBehavior]
 * @param headerSecondaryPreview 在 [UltraSwipeHeaderState.ReleaseToSecondary] 状态下是否可提前预览 Header 二级内容
 * @param footerSecondaryPreview 在 [UltraSwipeFooterState.ReleaseToSecondary] 状态下是否可提前预览 Footer 二级内容
 * @param headerSecondaryTriggerRate 触发 Header 二级内容的最小滑动比例，基于 [headerIndicator] 的高度；默认值：2
 * @param footerSecondaryTriggerRate 触发 Footer 二级内容的最小滑动比例，基于 [footerIndicator] 的高度；默认值：2
 * @param headerMaxOffsetRate 下拉时 [headerIndicator] 的最大滑动偏移比例，基于其自身高度；默认值：3
 * @param footerMaxOffsetRate 上拉时 [footerIndicator] 的最大滑动偏移比例，基于其自身高度；默认值：3
 * @param dragMultiplier 滑动时的阻力系数，值越小阻力越大；默认值：0.5
 * @param finishDelayMillis 完成状态的停留时长（毫秒），便于展示提示内容；默认值：500
 * @param vibrationEnabled 是否启用振动反馈。启用后，滑动偏移量达到阈值时将触发振动；默认值：false
 * @param vibrationMillis 触发刷新或加载时的振动时长（毫秒）；默认值：25
 * @param alwaysScrollable 是否始终允许滚动。设为 true 时，不受刷新/加载状态限制，始终可滚动；默认值：false
 * @param onCollapseScroll 可选回调，当 Header/Footer 收起时用于同步调整列表位置，消除视觉回弹
 * @param headerIndicator 下拉刷新时顶部显示的 Header 指示器
 * @param footerIndicator 上拉加载时底部显示的 Footer 指示器
 * @param headerSecondaryContent Header 二级内容（可选）
 * @param footerSecondaryContent Footer 二级内容（可选）
 * @param contentContainer [content] 的父容器，便于统一管理
 * @param content 可进行刷新或加载所包含的内容区域
 */
@Composable
fun SwipeRefresh(
    state: UltraSwipeRefreshState = rememberUltraSwipeRefreshState(),
    onRefresh: (() -> Unit)? = null,
    onLoadMore: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    headerScrollMode: NestedScrollMode = NestedScrollMode.FixedContent,
    footerScrollMode: NestedScrollMode = NestedScrollMode.FixedContent,
    @FloatRange(from = 0.0, fromInclusive = false)
    refreshTriggerRate: Float = UltraSwipeRefreshTheme.config.refreshTriggerRate,
    @FloatRange(from = 0.0, fromInclusive = false)
    loadMoreTriggerRate: Float = UltraSwipeRefreshTheme.config.loadMoreTriggerRate,
    headerSecondaryEnabled: Boolean = UltraSwipeRefreshTheme.config.headerSecondaryEnabled,
    footerSecondaryEnabled: Boolean = UltraSwipeRefreshTheme.config.footerSecondaryEnabled,
    headerSecondaryBehavior: SecondaryBehavior = UltraSwipeRefreshTheme.config.headerSecondaryBehavior,
    footerSecondaryBehavior: SecondaryBehavior = UltraSwipeRefreshTheme.config.footerSecondaryBehavior,
    headerSecondaryPreview: Boolean = UltraSwipeRefreshTheme.config.headerSecondaryPreview,
    footerSecondaryPreview: Boolean = UltraSwipeRefreshTheme.config.footerSecondaryPreview,
    @FloatRange(from = 1.0, fromInclusive = false)
    headerSecondaryTriggerRate: Float = UltraSwipeRefreshTheme.config.headerSecondaryTriggerRate,
    @FloatRange(from = 1.0, fromInclusive = false)
    footerSecondaryTriggerRate: Float = UltraSwipeRefreshTheme.config.footerSecondaryTriggerRate,
    @FloatRange(from = 1.0)
    headerMaxOffsetRate: Float = UltraSwipeRefreshTheme.config.headerMaxOffsetRate,
    @FloatRange(from = 1.0)
    footerMaxOffsetRate: Float = UltraSwipeRefreshTheme.config.footerMaxOffsetRate,
    @FloatRange(from = 0.0, to = 2.0, fromInclusive = false)
    dragMultiplier: Float = UltraSwipeRefreshTheme.config.dragMultiplier,
    @IntRange(from = 0, to = 2000)
    finishDelayMillis: Long = UltraSwipeRefreshTheme.config.finishDelayMillis,
    vibrationEnabled: Boolean = UltraSwipeRefreshTheme.config.vibrationEnabled,
    @IntRange(from = 1, to = 50)
    vibrationMillis: Long = UltraSwipeRefreshTheme.config.vibrationMillis,
    alwaysScrollable: Boolean = UltraSwipeRefreshTheme.config.alwaysScrollable,
    onCollapseScroll: (suspend (Float) -> Unit)? = null,
    headerIndicator: @Composable (UltraSwipeRefreshState) -> Unit = UltraSwipeRefreshTheme.config.headerIndicator,
    footerIndicator: @Composable (UltraSwipeRefreshState) -> Unit = UltraSwipeRefreshTheme.config.footerIndicator,
    headerSecondaryContent: (@Composable (UltraSwipeRefreshState) -> Unit)? = null,
    footerSecondaryContent: (@Composable (UltraSwipeRefreshState) -> Unit)? = null,
    contentContainer: @Composable (@Composable () -> Unit) -> Unit = {
        CompositionLocalProvider(LocalOverscrollFactory provides null, content = it)
    },
    content: @Composable (state: LazyListState) -> Unit,
) {

    val lazyListState = rememberLazyListState()

    UltraSwipeRefresh(
        state = state,
        onRefresh = {
            state.isRefreshing = true
            onRefresh?.invoke()
        },
        onLoadMore = {
            state.isLoading = true
            onLoadMore?.invoke()
        },
        modifier = modifier,
        headerScrollMode = headerScrollMode,
        footerScrollMode = footerScrollMode,
        refreshEnabled = onRefresh.isNotNull(),
        loadMoreEnabled = onLoadMore.isNotNull(),
        refreshTriggerRate = refreshTriggerRate,
        loadMoreTriggerRate = loadMoreTriggerRate,
        headerSecondaryEnabled = headerSecondaryEnabled,
        footerSecondaryEnabled = footerSecondaryEnabled,
        headerSecondaryBehavior = headerSecondaryBehavior,
        footerSecondaryBehavior = footerSecondaryBehavior,
        headerSecondaryPreview = headerSecondaryPreview,
        footerSecondaryPreview = footerSecondaryPreview,
        headerSecondaryTriggerRate = headerSecondaryTriggerRate,
        footerSecondaryTriggerRate = footerSecondaryTriggerRate,
        headerMaxOffsetRate = headerMaxOffsetRate,
        footerMaxOffsetRate = footerMaxOffsetRate,
        dragMultiplier = dragMultiplier,
        finishDelayMillis = finishDelayMillis,
        vibrationEnabled = vibrationEnabled,
        vibrationMillis = vibrationMillis,
        alwaysScrollable = alwaysScrollable,
        onCollapseScroll = {
            // 小于0时表示：由下拉刷新收起时触发的，大于0时表示：由上拉加载收起时触发的
            if (it > 0) {
                // 指示器收起时滚动列表位置，消除视觉回弹
                lazyListState.animateScrollBy(it)
            }
            onCollapseScroll?.invoke(it)
        },
        headerIndicator = headerIndicator,
        footerIndicator = footerIndicator,
        headerSecondaryContent = headerSecondaryContent,
        footerSecondaryContent = footerSecondaryContent,
        contentContainer = contentContainer
    ) {
        content.invoke(lazyListState)
    }
}

/**
 * 列表内容下拉刷新和上拉加载示例
 */
@Preview
@Composable
fun PreviewUltraSwipeRefreshList() {
    val state = rememberUltraSwipeRefreshState()
    var itemCount by remember { mutableIntStateOf(20) }
    val coroutineScope = rememberCoroutineScope()

    SwipeRefresh(
        state = state,
        onRefresh = {
            coroutineScope.launch {
                delay(2000.milliseconds)
                itemCount = 20
                state.isRefreshing = false
            }
        },
        onLoadMore = {
            coroutineScope.launch {
                delay(2000.milliseconds)
                itemCount += 20
                state.isLoading = false
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            state = it,
        ) {
            repeat(itemCount) {
                item {
                    Text(
                        text = "UltraSwipeRefresh列表Item${it + 1}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        color = Color(0xFF333333),
                        fontSize = 16.sp
                    )
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .background(color = Color.White)
                    )
                }
            }
        }
    }
}


/**
 * 非列表内容下拉刷新和上拉加载示例
 * 核心: 在内容的根节点设置Modifier.verticalScroll(state = rememberScrollState())
 */
@Preview
@Composable
fun PreviewUltraSwipeRefreshNoList() {
    val state = rememberUltraSwipeRefreshState()
    var itemCount by remember { mutableIntStateOf(20) }
    val coroutineScope = rememberCoroutineScope()

    SwipeRefresh(
        state = state,
        onRefresh = {
            coroutineScope.launch {
                delay(2000.milliseconds)
                itemCount = 20
                state.isRefreshing = false
            }
        },
        onLoadMore = {
            coroutineScope.launch {
                delay(2000.milliseconds)
                itemCount += 20
                state.isLoading = false
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            repeat(itemCount) {
                Text(
                    text = "UltraSwipeRefresh列表Item${it + 1}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = Color(0xFF333333),
                    fontSize = 16.sp
                )
                Spacer(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .background(color = Color.White)
                )
            }
        }
    }
}