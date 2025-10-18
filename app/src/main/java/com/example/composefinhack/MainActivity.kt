package com.example.composefinhack

import android.R.attr.fontWeight
import android.R.attr.singleLine
import android.R.attr.text
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.composefinhack.ui.theme.BgGrey
import com.example.composefinhack.ui.theme.BgOrange
import com.example.composefinhack.ui.theme.ButtonBlue
import com.example.composefinhack.ui.theme.TextColor


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                //AppNavigation()
                WelcomeScreen2()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Welcome : Screen("welcome", "Welcome", Icons.Default.Home)
    object Feed : Screen("feed", "Лента", Icons.Default.Home)
    object Events : Screen("events", "Ивенты", Icons.Default.Event)
    object Subscriptions : Screen("subscriptions", "Подписки", Icons.Default.Subscriptions)
    object Profile : Screen("profile", "Профиль", Icons.Default.Person)
}

//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//
//    val bottomBarRoutes = listOf(Screen.Feed.route, Screen.Events.route, Screen.Subscriptions.route, Screen.Profile.route)
//
//    Scaffold(
//        bottomBar = {
//            val navBackStackEntry by navController.currentBackStackEntryAsState()
//            val currentRoute = navBackStackEntry?.destination?.route
//            if (currentRoute in bottomBarRoutes) {
//                BottomNavigationBar(navController = navController, currentDestination = navBackStackEntry?.destination)
//            }
//        }
//    ) { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = Screen.Welcome.route,
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            composable(Screen.Welcome.route) {
//                WelcomeScreen(navController)
//            }
//
//            composable(Screen.Feed.route) { Feed() }
//            composable(Screen.Events.route) { Events() }
//            composable(Screen.Subscriptions.route) { Subscriptions() }
//            composable(Screen.Profile.route) { Profile() }
//        }
//    }
//}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentDestination: NavDestination?) {
    val items = listOf(Screen.Feed, Screen.Events, Screen.Subscriptions, Screen.Profile)

    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.route == item.route

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {

                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true

                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun WelcomeScreen2() {
    var loginText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize())
        {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(BgOrange)
            ) {
                Column(modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 180.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp))
                {
                    Text(text = "Cоздай свой\nаккаунт",
                        fontSize = 30.sp, textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold, color = TextColor)
                    Text(text = "Введи свой email и пароль", color = TextColor)

                }


            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(BgGrey)
            )
        }

        Box(modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
            .height(300.dp)
            .align(Alignment.Center)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                OutlinedTextField(
                    value = loginText,
                    onValueChange = { loginText = it },
                    modifier = Modifier.fillMaxWidth().padding(top=25.dp, start = 15.dp, end = 15.dp).height(60.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(
                        12.dp
                    ),
                    label = { Text("Логин") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = passwordText,
                    onValueChange = { passwordText = it },
                    modifier = Modifier.fillMaxWidth().padding(15.dp).height(60.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(
                        12.dp
                    ),
                    label = { Text("Пароль") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                            )
                        }
                    }
                )

                ClickableText(
                    text = AnnotatedString("Забыли пароль?"),
                    onClick = {
                        // TODO: переход на восстановление пароля
                    },
                    modifier = Modifier.align(Alignment.End).padding(top = 3.dp, end = 10.dp),
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF1E88E5),
                        textDecoration = TextDecoration.Underline
                    )
                )
        }


    }
    }
}



//@Composable
//fun WelcomeScreen(navController: NavHostController) {
//    var loginText by remember { mutableStateOf("") }
//    var passwordText by remember { mutableStateOf("") }
//    var passwordVisible by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .padding(37.dp, 35.dp)
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.fillMaxWidth(),
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.applogo),
//                contentDescription = "logo",
//                modifier = Modifier
//                    .size(140.dp)
//                    .padding(top = 52.dp)
//            )
//
//            Text(
//                modifier = Modifier.padding(top = 44.dp).align(Alignment.CenterHorizontally),
//                text = "СуперПриложение",
//                fontSize = 30.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//
//            ) {
//                OutlinedTextField(
//                    value = loginText,
//                    onValueChange = { loginText = it },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = androidx.compose.foundation.shape.RoundedCornerShape(
//                        topStart = 12.dp,
//                        topEnd = 12.dp,
//                        bottomStart = 0.dp,
//                        bottomEnd = 0.dp
//                    ),
//                    label = { Text("Логин") },
//                    singleLine = true
//                )
//
//                OutlinedTextField(
//                    value = passwordText,
//                    onValueChange = { passwordText = it },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = androidx.compose.foundation.shape.RoundedCornerShape(
//                        topStart = 0.dp,
//                        topEnd = 0.dp,
//                        bottomStart = 12.dp,
//                        bottomEnd = 12.dp
//                    ),
//                    label = { Text("Пароль") },
//                    singleLine = true,
//                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                    trailingIcon = {
//                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                            Icon(
//                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
//                                contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
//                            )
//                        }
//                    }
//                )
//
//                ClickableText(
//                    text = AnnotatedString("Забыли пароль?"),
//                    onClick = {
//                        // TODO: переход на восстановление пароля
//                    },
//                    modifier = Modifier.align(Alignment.End).padding(top = 3.dp),
//                    style = androidx.compose.ui.text.TextStyle(
//                        color = Color(0xFF1E88E5),
//                        textDecoration = TextDecoration.Underline
//                    )
//                )
//
//                Button(
//                    onClick = {
//                        // навигация в Feed и удаление Welcome из backstack
//                        navController.navigate(Screen.Feed.route) {
//                            popUpTo(Screen.Welcome.route) { inclusive = true }
//                        }
//                    },
//                    modifier = Modifier
//                        .padding(top = 50.dp)
//                        .fillMaxWidth()
//                        .height(60.dp),
//                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
//                ) {
//                    Text("Войти", fontSize = 17.sp)
//                }
//
//                Text(
//                    modifier = Modifier
//                        .padding(top = 44.dp)
//                        .align(alignment = Alignment.CenterHorizontally),
//                    text = "или",
//                    fontSize = 15.sp,
//                    color = Color.DarkGray
//                )
//
//                ClickableText(
//                    text = AnnotatedString("Зарегистрироваться"),
//                    onClick = {
//                        // TODO: переход на регистрацию
//                    },
//                    modifier = Modifier
//                        .align(Alignment.CenterHorizontally)
//                        .padding(top = 44.dp),
//                    style = androidx.compose.ui.text.TextStyle(
//                        color = Color(0xFF1E88E5),
//                        textDecoration = TextDecoration.Underline
//                    )
//                )
//            }
//        }
//    }
//}


@Composable
fun Feed() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text("Тут типо лента", fontSize = 28.sp)
        }
    }
}

@Composable
fun Events() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text("Тут типо ивенты", fontSize = 28.sp)
        }
    }
}

@Composable
fun Subscriptions() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text("Тут типо подписки", fontSize = 28.sp)
        }
    }
}

@Composable
fun Profile() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text("Тут типо профиль", fontSize = 28.sp)
        }
    }
 }



