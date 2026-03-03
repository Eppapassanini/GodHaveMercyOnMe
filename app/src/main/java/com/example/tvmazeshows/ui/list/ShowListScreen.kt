package com.example.tvmazeshows.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tvmazeshows.R
import com.example.tvmazeshows.domain.model.Show
import com.example.tvmazeshows.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowsListScreen(
    viewModel: ShowsListViewModel,
    onShowClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
            is UiState.Content -> ShowsList(
                shows = state.data,
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

@Composable
fun ShowsList(shows: List<Show>, onShowClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(shows) { show ->
            ShowListItem(show = show, onClick = { onShowClick(show.id) })
        }
    }
}

@Composable
fun ShowListItem(show: Show, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            show.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = show.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxSize()
            ) {
                Text(
                    text = show.name.firstOrNull()?.toString() ?: "?",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                show.networkName?.let { network ->
                    Text(
                        text = network,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                show.rating?.let { rating ->
                    Text(
                        text = "⭐ $rating",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.error_loading),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        onRetry?.let {
            Button(onClick = it) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}