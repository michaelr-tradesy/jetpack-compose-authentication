package com.example.jetpackcomposecodelab101

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposecodelab101.ui.theme.JetpackComposeCodeLab101Theme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ComposeView(this).also {
            setContentView(it)
        }.setContent {
            JetpackComposeCodeLab101Theme {
                MyApp()
            }
        }
    }

    @Composable
    private fun MyApp() {
        val shouldShowOnBoarding = rememberSaveable { mutableStateOf(true) }

        if (shouldShowOnBoarding.value) {
            ShowOnBoardingScreen(onContinueClicked = { shouldShowOnBoarding.value = false })
        } else {
            ShowGreetings()
        }
    }

    @Composable
    private fun ShowOnBoardingScreen(onContinueClicked: () -> Unit) {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.welcome))
                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    onClick = onContinueClicked
                ) {
                    Text(stringResource(R.string.continue_text))
                }
            }
        }
    }


    @Composable
    private fun ShowGreetings(names: List<String> = List(1000) { "$it" }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                items(items = names) { name ->
                    Greeting(name = name)
                }
            }
        }
    }

    @Composable
    private fun Greeting(name: String) {
        Card(
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            CardContent(name)
        }
    }

    @Composable
    private fun CardContent(name: String) {
        // "remember" is used to guard against recomposition, so the state is not reset.
        val expanded = rememberSaveable { mutableStateOf(false) }
        val extraPadding by animateDpAsState(
            if (expanded.value) 48.dp else 0.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            color = MaterialTheme.colors.primary
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = extraPadding.coerceAtLeast(0.dp))
                        .weight(1f)
                ) {
                    Text(text = stringResource(R.string.hello_text))
                    Text(
                        text = "$name!",
                        style = MaterialTheme.typography.h4.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    if (expanded.value) {
                        Text(
                            text = stringResource(R.string.expanded_text).repeat(4),
                        )
                    }
                }
                IconButton(onClick = { expanded.value = !expanded.value }) {
                    Icon(
                        imageVector = if (expanded.value) {
                            Icons.Filled.ExpandLess
                        } else {
                            Icons.Filled.ExpandMore
                        },
                        contentDescription = if (expanded.value) {
                            stringResource(R.string.show_less)
                        } else {
                            stringResource(R.string.show_more)
                        }

                    )
                }
            }
        }
    }

    @Preview(
        fontScale = 1.5f,
        name = "On Boarding Light Mode",
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        showSystemUi = true,
        showBackground = true,
        widthDp = 320,
        heightDp = 320
    )
    @Composable
    fun OnBoardingLightPreview() {
        JetpackComposeCodeLab101Theme {
            ShowOnBoardingScreen(onContinueClicked = {})
        }
    }

    @Preview(
        fontScale = 1.5f,
        name = "On Boarding Dark Mode",
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        showSystemUi = true,
        showBackground = true,
        widthDp = 320,
        heightDp = 320
    )
    @Composable
    fun OnBoardingDarkModePreview() {
        JetpackComposeCodeLab101Theme {
            ShowOnBoardingScreen(onContinueClicked = {})
        }
    }

    @Preview(
        fontScale = 1.5f,
        name = "Light Mode",
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        showSystemUi = true,
        showBackground = true,
        widthDp = 320,
        heightDp = 320
    )
    @Composable
    fun ShowGreetingsInLightModePreview() {
        JetpackComposeCodeLab101Theme {
            ShowGreetings()
        }
    }

    @Preview(
        fontScale = 1.5f,
        name = "Dark Mode",
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        showSystemUi = true,
        showBackground = true,
        widthDp = 320,
        heightDp = 320
    )
    @Composable
    fun ShowGreetingsInDarkModePreview() {
        JetpackComposeCodeLab101Theme {
            ShowGreetings()
        }
    }
}