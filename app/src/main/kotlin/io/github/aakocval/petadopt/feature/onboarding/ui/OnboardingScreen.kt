package io.github.aakocval.petadopt.feature.onboarding.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing
import kotlinx.coroutines.launch

private data class OnboardingPage(
    @param:StringRes val title: Int,
    @param:StringRes val body: Int,
    @param:DrawableRes val icon: Int,
)

private val Pages =
    listOf(
        OnboardingPage(R.string.onboarding_page1_title, R.string.onboarding_page1_body, PetAdoptIcons.Paw),
        OnboardingPage(R.string.onboarding_page2_title, R.string.onboarding_page2_body, PetAdoptIcons.Heart),
        OnboardingPage(R.string.onboarding_page3_title, R.string.onboarding_page3_body, PetAdoptIcons.MapPin),
    )

@Composable
fun OnboardingRoute(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    OnboardingScreen(onFinish = viewModel::onFinish, modifier = modifier)
}

/** Three swipeable pages; Skip or Get started finishes. Page position is pager state, not ViewModel state. */
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { Pages.size },
) {
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == Pages.lastIndex

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding()
                .padding(Spacing.lg),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.CenterEnd) {
            if (!isLastPage) TextButton(onClick = onFinish) { Text(stringResource(R.string.onboarding_skip)) }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { index ->
            PageContent(Pages[index], tileKey = "onboarding-$index")
        }
        PageIndicator(pagerState)
        Spacer(Modifier.height(Spacing.xl))
        Button(
            onClick = {
                if (isLastPage) {
                    onFinish()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1,
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(stringResource(if (isLastPage) R.string.onboarding_get_started else R.string.onboarding_next))
        }
    }
}

@Composable
private fun PageContent(
    page: OnboardingPage,
    tileKey: String,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(200.dp)
                    .background(PetAdoptTheme.colors.tileFor(tileKey), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(page.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(88.dp),
            )
        }
        Spacer(Modifier.height(Spacing.xxl))
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = stringResource(page.body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PageIndicator(pagerState: PagerState) {
    val description = stringResource(R.string.onboarding_page_indicator, pagerState.currentPage + 1, Pages.size)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
    ) {
        repeat(Pages.size) { index ->
            val color by animateColorAsState(
                if (index ==
                    pagerState.currentPage
                ) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                label = "indicator",
            )
            Box(Modifier.size(8.dp).background(color, CircleShape))
        }
    }
}

@PreviewLightDark
@Composable
private fun OnboardingScreenPreview() {
    PetAdoptTheme { Surface { OnboardingScreen(onFinish = {}) } }
}
