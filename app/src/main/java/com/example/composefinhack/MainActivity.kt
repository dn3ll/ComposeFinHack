package com.example.composefinhack

import android.R.attr.fontWeight
import android.R.attr.singleLine
import android.R.attr.text
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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
import com.example.composefinhack.ui.theme.TopPanelPurple
import androidx.compose.ui.res.vectorResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AppNavigation()
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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    val bottomBarRoutes = listOf(Screen.Feed.route, Screen.Events.route, Screen.Subscriptions.route, Screen.Profile.route)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute in bottomBarRoutes) {
                BottomNavigationBar(navController = navController, currentDestination = navBackStackEntry?.destination)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(navController)
            }

            composable(Screen.Feed.route) { Feed() }
            composable(Screen.Events.route) { Events() }
            composable(Screen.Subscriptions.route) { Subscriptions() }
            composable(Screen.Profile.route) { Profile() }
        }
    }
}

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


@Composable
fun WelcomeScreen(navController: NavHostController) {
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
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Text(text = "Cоздай свой",
                        fontSize = 30.sp, textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold, color = TextColor)
                    Text(text = "аккаунт",
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

                Button(
                    onClick = {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
                ) {
                    Text("Войти", fontSize = 17.sp)
                }


                ClickableText(
                    text = AnnotatedString("Зарегистрироваться?"),
                    onClick = {
                        // TODO: переход на восстановление пароля
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF1E88E5),
                        textDecoration = TextDecoration.Underline
                    )
                )

                }
        }


    }
}





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

@Preview
@Composable
fun Profile() {
    var university = "НИТУ МИСИС"
    var age = 20
    var aboutMe = "Студент первого курса, побеждал на 20 хакатонах"
    Box(modifier = Modifier.fillMaxSize())
    {
        Column(){
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(TopPanelPurple))
            {
                Text(
                    text = "АНКЕТА",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 65.dp),
                    fontSize =35.sp,
                    fontWeight = FontWeight.Bold
                )

            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                   .background(BgGrey))
            {
                Column(modifier = Modifier.fillMaxSize()
                    .padding(top = 20.dp))
                {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp))
                        {
                            Image(
                                painter = painterResource(id = R.drawable.anketa),
                                contentDescription = "Первая картинка",
                                modifier = Modifier
                                    .width(250.dp)
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            Image(
                                painter = painterResource(id = R.drawable.education),
                                contentDescription = "Вторая картинка",
                                modifier = Modifier
                                    .width(250.dp)
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    Box(modifier = Modifier.fillMaxSize()
                        .padding(top=30.dp, start = 20.dp, end = 20.dp, bottom = 40.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                    ) {

                        Row(modifier = Modifier.padding(top=20.dp, start = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.avatar),
                                contentDescription = "Аватарка",
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(185.dp)
                                    .clip(RoundedCornerShape(12.dp)
                                    )
                            )
                            Column(){
                                Text(text = "Образование", fontSize = 25.sp, fontWeight = FontWeight.Bold )
                                Text(text = "НИТУ МИСИС", fontSize = 15.sp, fontWeight = FontWeight.Bold )
                            }

                        }




                    }


                }
            }
        }
    }

}




