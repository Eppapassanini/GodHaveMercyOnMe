package com.example.tvmazeshows.ui.detail

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tvmazeshows.R
import com.example.tvmazeshows.domain.model.Show
import com.example.tvmazeshows.ui.list.ErrorScreen
import com.example.tvmazeshows.ui.list.LoadingScreen
import com.example.tvmazeshows.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    viewModel: ShowDetailViewModel,
    onShowClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val relatedShowsState by viewModel.relatedShowsState.collectAsState()
    val tabs = viewModel.tabs


    LaunchedEffect(uiState) {
        if (uiState is UiState.Content) {
            viewModel.loadRelatedShows((uiState as UiState.Content).data)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.app_name))
                        Text(
                            text = stringResource(R.string.variant_code),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is UiState.Loading -> LoadingScreen(modifier = Modifier.padding(padding))
            is UiState.Content -> ShowDetailContent(
                show = state.data,
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = viewModel::selectTab,
                relatedShowsState = relatedShowsState,
                onShowClick = onShowClick,
                modifier = Modifier.padding(padding)
            )
            is UiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = state.retryAction,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailContent(
    show: Show,
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    relatedShowsState: UiState<List<Show>>,
    onShowClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ShowHeader(show = show)

        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(title) }
                )
            }
        }

        TabContent(
            show = show,
            selectedTab = selectedTab,
            relatedShowsState = relatedShowsState,
            onShowClick = onShowClick,
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        )
    }
}

@Composable
fun ShowHeader(show: Show) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        show.imageUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = show.name,
                modifier = Modifier
                    .size(200.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = show.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        show.rating?.let { rating ->
            Text(
                text = "⭐ Rating: $rating",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TabContent(
    show: Show,
    selectedTab: Int,
    modifier: Modifier = Modifier,
    relatedShowsState: UiState<List<Show>> = UiState.Loading,
    onShowClick: (Int) -> Unit = {}
) {
    when (selectedTab) {
        0 -> InfoTab(show = show, modifier = modifier)
        1 -> RelatedTab(
            modifier = modifier,
            relatedShowsState = relatedShowsState,
            onShowClick = onShowClick
        )
        2 -> LinksTab(show = show, modifier = modifier)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoTab(show: Show, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Summary", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(
            text = show.summary ?: stringResource(R.string.no_summary),
            style = MaterialTheme.typography.bodyMedium
        )

        if (show.genres.isNotEmpty()) {
            Text(text = "Genres", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                show.genres.forEach { genre ->
                    AssistChip(
                        onClick = { },
                        label = { Text(genre) }
                    )
                }
            }
        } else {
            Text(text = stringResource(R.string.no_genres), style = MaterialTheme.typography.bodySmall)
        }

        HorizontalDivider()

        InfoRow(label = "Status", value = show.status ?: stringResource(R.string.unknown))
        show.premiered?.let { InfoRow(label = "Premiered", value = it) }
        show.language?.let { InfoRow(label = "Language", value = it) }
        show.networkName?.let { InfoRow(label = "Network", value = it) }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun RelatedTab(
    modifier: Modifier = Modifier,
    relatedShowsState: UiState<List<Show>> = UiState.Loading,
    onShowClick: (Int) -> Unit = {}
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Related Shows",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        when (relatedShowsState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Content -> {
                if (relatedShowsState.data.isEmpty()) {
                    Text(
                        text = "No related shows found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(relatedShowsState.data) { relatedShow ->
                            RelatedShowCard(
                                show = relatedShow,
                                onClick = { onShowClick(relatedShow.id) }
                            )
                        }
                    }
                }
            }
            is UiState.Error -> {
                ErrorScreen(
                    message = relatedShowsState.message,
                    onRetry = relatedShowsState.retryAction
                )
            }
        }
    }
}



@Composable
fun RelatedShowCard(show: Show, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            show.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = show.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = show.name.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                show.rating?.let { rating ->
                    Text(
                        text = "⭐ $rating",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (show.genres.isNotEmpty()) {
                    Text(
                        text = show.genres.take(3).joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LinksTab(show: Show, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("External Links", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

        LinkButton(
            title = "TVmaze Page",
            url = show.url,
            onClick = { openUrl(context, show.url) }
        )

        show.officialSite?.takeIf { it.isNotBlank() }?.let { url ->
            LinkButton(
                title = "Official Site",
                url = url,
                onClick = { openUrl(context, url) }
            )
        }

        show.externalLinks.imdb?.let { imdbId ->
            val imdbUrl = "https://www.imdb.com/title/$imdbId"
            LinkButton(
                title = "IMDb",
                url = imdbUrl,
                onClick = { openUrl(context, imdbUrl) }
            )
        }

        show.externalLinks.thetvdb?.let { tvdbId ->
            val tvdbUrl = "https://thetvdb.com/dereferrer/series/$tvdbId"
            LinkButton(
                title = "TheTVDB",
                url = tvdbUrl,
                onClick = { openUrl(context, tvdbUrl) }
            )
        }
    }
}

@Composable
fun LinkButton(title: String, url: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(title)
        Icon(
            androidx.compose.material.icons.Icons.Default.OpenInNew,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)

    } catch (e: Exception) {
        Toast.makeText(context, "Whoops! I can't do it", Toast.LENGTH_SHORT).show()
    }
}
