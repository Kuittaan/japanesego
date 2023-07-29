package org.kuittaan.japanesego

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController

class GameView {

    @Composable
    fun createMenu() {

        var selectedItem by remember { mutableStateOf(0) }
        val navController = rememberNavController()

        Scaffold(
            bottomBar = {
                androidx.compose.material3.NavigationBar(
                    containerColor = colorResource(id = R.color.red_light),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)),
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate("settings")
                        },
                        //Other parts will be the same
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings Icon",
                                modifier = Modifier.size(42.dp),
                                tint = colorResource(id = R.color.white_red)
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate("menu")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "Menu Icon",
                                modifier = Modifier.size(42.dp),
                                tint = colorResource(id = R.color.white_red)
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate("profile")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profile Icon",
                                modifier = Modifier.size(42.dp),
                                tint = colorResource(id = R.color.white_red)
                            )
                        }
                    )
                }
            },
            content = { innerPadding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                ) {

                }
            }
        )
    }

    @Preview
    @Composable
    fun SimpleComposablePreview() {
        createMenu()
    }
}