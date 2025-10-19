package com.example.composefinhack

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import android.content.Context
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.material3.Slider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController

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

// Simple user storage via DataStore (demo only; don't store passwords like this in production)
private val Context.userDataStore by preferencesDataStore(name = "user_prefs")
private val KEY_FULL_NAME = stringPreferencesKey("full_name")
private val KEY_PHONE = stringPreferencesKey("phone")

sealed class Screen(
    val route: String,
    val label: String = route,
    val icon: ImageVector? = null
) {
    object Welcome : Screen("welcome", "Welcome", Icons.Default.Home)
    object Feed : Screen("feed", "Лента", Icons.Default.Home)
    object Events : Screen("events", "Ивенты", Icons.Default.Event)
    object Subscriptions : Screen("subscriptions", "Подписки", Icons.Default.Subscriptions)
    object Profile : Screen("profile", "Профиль", Icons.Default.Person)

    // Для экранов, которые не отображаются в bottom bar, иконку можно не указывать
    object Anketa : Screen("anketa", "Анкета")
    object Education : Screen("education", "Образование")
    object Registration : Screen("registration", "Регистрация")
    object CreatePost : Screen("create_post", "Создать пост")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Храним ленту постов на уровне навигации, чтобы экран создания мог добавлять новые записи
    val feedPosts = remember {
        mutableStateListOf(
            FeedPost(
                id = "1",
                title = "«МИКРОН» — открывает двери для студентов!",
                subtitle = "Экскурсия по крупнейшему микроэлектронному производству России, знакомство с технологиями чипов и процессом фотолитографии.",
                category = "#Экскурсия",
                date = "24.10",
                skills = "python, scratch",
                difficulty = 2,
                imageRes = R.drawable.hack1,
                from = "АО Микрон"
            ),
            FeedPost(
                id = "2",
                title = "«Умные лифты будущего»",
                subtitle = "Приглашаем проектировщиков, архитекторов и разработчиков на эксклюзивный воркшоп о трансформации лифтовой индустрии.",
                category = "#Воркшоп",
                date = "03.12",
                skills = "Основы программирования, Arduino",
                difficulty = 3,
                imageRes = R.drawable.hack2,
                from = "LiftLab"
            ),
            FeedPost(
                id = "3",
                title = "Кабельный завод «Спецкабель»",
                subtitle = "Погружение в мир современного производства: знакомство с работой инженеров и технологов и тем, как создаются сложные электроматериалы.",
                category = "#Экскурсия",
                date = "20.12",
                skills = "python, scratch",
                difficulty = 2,
                imageRes = R.drawable.banner,
                from = "Моспром"
            ),
            FeedPost(
                id = "4",
                title = "«Умная открытка» с чипом RFID/NFC",
                subtitle = "Соберём интерактивную открытку, которая с помощью чипа передаст цифровое сообщение (видео, поздравление, ссылку) на смартфон.",
                category = "#Проект",
                date = "10.10",
                skills = "Основы программирования",
                difficulty = 3,
                imageRes = R.drawable.internship,
                from = "Хакатоны"
            )
        )
    }

    val bottomBarRoutes = listOf(
        Screen.Feed.route,
        Screen.Events.route,
        Screen.Subscriptions.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val showBottomBar = currentRoute in bottomBarRoutes || currentRoute == Screen.Education.route

            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    currentDestination = navBackStackEntry?.destination,
                    activeRoute = if (currentRoute == Screen.Education.route) Screen.Profile.route else currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Welcome.route) { WelcomeScreen(navController) }
            composable(Screen.Registration.route) { RegistrationScreen(navController) }

            composable(Screen.Feed.route) {
                Feed(
                    posts = feedPosts,
                    onCreatePost = { navController.navigate(Screen.CreatePost.route) }
                )
            }
            composable(Screen.Events.route) { Events() }
            composable(Screen.Subscriptions.route) { Subscriptions() }

            composable(Screen.Profile.route) { Profile(navController) }
            composable(Screen.Education.route) { Education(navController) }

            composable(Screen.CreatePost.route) {
                CreatePostScreen(
                    onCancel = { navController.popBackStack() },
                    onSubmit = { post ->
                        feedPosts.add(0, post) // добавить в начало ленты
                        navController.popBackStack() // вернуться в ленту
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController, currentDestination: NavDestination?,
                        activeRoute: String? = currentDestination?.route) {
    val items = listOf(Screen.Feed, Screen.Events, Screen.Subscriptions, Screen.Profile)

    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.route == item.route

            NavigationBarItem(
                icon = { item.icon?.let { Icon(it, contentDescription = item.label) } },
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
                    .background(BgGrey)
            ) {
                Column(modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 180.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Text(text = "Введите ваши",
                        fontSize = 30.sp, textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "данные",
                        fontSize = 30.sp, textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "email и пароль", color = TextColor)

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
            .background(BgGrey)
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
                    colors = ButtonDefaults.buttonColors(containerColor = BgOrange)
                ) {
                    Text("Войти", fontSize = 17.sp)
                }


                ClickableText(
                    text = AnnotatedString("Зарегистрироваться?"),
                    onClick = {
                        navController.navigate(Screen.Registration.route)
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
fun Feed(
    posts: SnapshotStateList<FeedPost>,
    onCreatePost: () -> Unit
) {
    // Tab state: 0 – Подписки, 1 – Мои интересы
    var selectedTab by remember { mutableStateOf(0) }

    val communities = remember {
        mutableStateListOf(
            Community(
                id = "c1",
                name = "Предприниматели",
                followers = "1.2M Followers",
                avatarRes = R.drawable.news_author1,
                isFollowing = true
            ),
            Community(
                id = "c2",
                name = "Хакатоны",
                followers = "959K Followers",
                avatarRes = R.drawable.news_author2,
                isFollowing = false
            ),
            Community(
                id = "c3",
                name = "Моспром",
                followers = "325K Followers",
                avatarRes = R.drawable.news_author3,
                isFollowing = true
            ),
            Community(
                id = "c4",
                name = "Микрон",
                followers = "21K Followers",
                avatarRes = R.drawable.news_author4,
                isFollowing = false
            ),
            Community(
                id = "c5",
                name = "IT",
                followers = "18K Followers",
                avatarRes = R.drawable.news_author5,
                isFollowing = false
            ),
            Community(
                id = "c6",
                name = "MSN",
                followers = "15K Followers",
                avatarRes = R.drawable.news_author6,
                isFollowing = false
            )
        )
    }

    // На всякий случай фон как на других экранах
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgGrey)
        ) {
            item { FeedHeader(onAddClick = onCreatePost) }
            item { FeedTabs(selectedTab = selectedTab, onSelect = { selectedTab = it }) }

            if (selectedTab == 0) {
                items(posts, key = { it.id }) { post ->
                    FeedCard(post = post)
                }
            } else {
                items(communities, key = { it.id }) { community ->
                    CommunityCard(
                        community = community,
                        onToggleFollow = {
                            val idx = communities.indexOfFirst { it.id == community.id }
                            if (idx != -1) {
                                val current = communities[idx]
                                communities[idx] = current.copy(isFollowing = !current.isFollowing)
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun FeedHeader(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(TopPanelPurple)
    ) {
        // Кнопка «+» в правом верхнем углу
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable { onAddClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Добавить пост")
        }

        Text(
            text = "ЛЕНТА НОВОСТЕЙ",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 30.dp),
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FeedTabs(selectedTab: Int, onSelect: (Int) -> Unit) {
    // Простые «сегменты» под заголовком: Подписки / Мои интересы
    val selectedColor = Color(0xFFDDEB87)  // мягкий зелёный, как на макете
    val unselectedColor = Color(0xFFF1F2F6)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgGrey)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (selectedTab == 0) selectedColor else unselectedColor)
                .clickable { onSelect(0) },
            contentAlignment = Alignment.Center
        ) { Text("Подписки", fontSize = 16.sp, fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal) }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (selectedTab == 1) selectedColor else unselectedColor)
                .clickable { onSelect(1) },
            contentAlignment = Alignment.Center
        ) { Text("Мои интересы", fontSize = 16.sp, fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal) }
    }
}

data class FeedPost(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val date: String,
    val skills: String,
    val difficulty: Int, // 0..5
    val imageRes: Int? = null,
    val from: String? = null
)

@Composable
private fun FeedCard(post: FeedPost) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Верхняя строка: слева картинка, справа дата
            Row(modifier = Modifier.fillMaxWidth()) {
                if (post.imageRes != null) {
                    Image(
                        painter = painterResource(id = post.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(92.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEDEDED)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Без фото", fontSize = 12.sp, color = Color(0xFF6B6B6B))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Категория в виде плашки
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BgOrange)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) { Text(post.category, color = Color.White, fontSize = 12.sp) }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = post.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (!post.from.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "от ${post.from}", fontSize = 12.sp, color = Color(0xFF5C5C5C))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = post.subtitle, fontSize = 13.sp)
                }

                Text(
                    text = post.date,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Top),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Навыки: ${post.skills}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            val stars = "★".repeat(post.difficulty.coerceIn(0, 5)) + "☆".repeat(5 - post.difficulty.coerceIn(0, 5))
            Text(text = "Уровень сложности: $stars", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { /* TODO: регистрация */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BgOrange)
                ) {
                    Text("Зарегистрироваться", fontSize = 14.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onCancel: () -> Unit,
    onSubmit: (FeedPost) -> Unit
) {
    var from by rememberSaveable { mutableStateOf("") } // клуб/организация (обязательное поле)
    var title by rememberSaveable { mutableStateOf("") }
    var subtitle by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("#Событие") }
    var date by rememberSaveable { mutableStateOf("") }
    var skills by rememberSaveable { mutableStateOf("") }
    var difficulty by rememberSaveable { mutableStateOf(0f) }
    val isValid = from.isNotBlank() && title.isNotBlank()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Новый пост") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = from,
                onValueChange = { from = it },
                singleLine = true,
                label = { Text("Клуб или организация *") },
                placeholder = { Text("Например, АО Микрон") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                label = { Text("Заголовок *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Краткое описание") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                singleLine = true,
                label = { Text("Категория (тег)") },
                placeholder = { Text("#Воркшоп / #Экскурсия / #Проект") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                singleLine = true,
                label = { Text("Дата") },
                placeholder = { Text("например, 12.12") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = skills,
                onValueChange = { skills = it },
                singleLine = true,
                label = { Text("Навыки") },
                placeholder = { Text("Python, Arduino…") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Сложность: ${difficulty.toInt()}/5", fontWeight = FontWeight.Medium)
            Slider(
                value = difficulty,
                onValueChange = { difficulty = it },
                valueRange = 0f..5f,
                steps = 4
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    onSubmit(
                        FeedPost(
                            id = "new_${System.currentTimeMillis()}",
                            title = title.trim(),
                            subtitle = subtitle.trim(),
                            category = if (category.isBlank()) "#Событие" else category.trim(),
                            date = date.ifBlank { "-" },
                            skills = skills.ifBlank { "-" },
                            difficulty = difficulty.toInt().coerceIn(0, 5),
                            imageRes = null, // пользовательские посты без картинки
                            from = from.trim()
                        )
                    )
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BgOrange)
            ) {
                Text("Опубликовать", fontSize = 16.sp)
            }
        }
    }
}

@Preview
@Composable
fun Events() {
    var query by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(TopPanelPurple)
            ) {
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Text(
                        text = "КУДА ПОЙТИ",
                        modifier = Modifier
                            .padding(start = 10.dp, bottom = 3.dp),
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "ПРОКАЧАТЬСЯ",
                        modifier = Modifier
                            .padding(start = 10.dp, bottom = 3.dp),
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )

                    TextField(
                        value = query, // <-- здесь должна быть та же переменная
                        onValueChange = { query = it },
                        placeholder = { Text("Найти событие или место") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
            Box(modifier = Modifier.fillMaxSize()
                .background(BgGrey))
            {
                Column(){
                    Text(
                        text = "// СТАЖИРОВКИ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp, top = 18.dp)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.internship),
                        contentDescription = "Первая картинка",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp)
                            )
                    )

                    Text(
                        text = "// ХАКАТОНЫ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top=16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hack1),
                            contentDescription = "Первая картинка",
                            modifier = Modifier
                                .width(200.dp)
                                .height(140.dp)

                                .clip(RoundedCornerShape(12.dp)
                                )
                        )

                        Image(
                            painter = painterResource(id = R.drawable.hack2),
                            contentDescription = "Вторая картинка",
                            modifier = Modifier
                                .width(160.dp)
                                .height(140.dp)

                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.banner),
                        contentDescription = "Баннер",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp)
                            )
                    )

                }
            }
        }
    }
}


@Composable
fun Subscriptions() {
    // 0 – От организаций, 1 – От пользователей
    var selectedTab by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    // Данные клубов «От организаций»
    val orgClubs = remember {
        mutableStateListOf(
            Club(
                id = "o1",
                title = "Клуб робототехники",
                from = "От Моспрома • 1,2 тыс. участников",
                description = "Общение, обмен опытом и совместные проекты в сфере робототехники.",
                tag = "#Роботы",
                bannerRes = R.drawable.club_back1
            ),
            Club(
                id = "o2",
                title = "Клуб хакатонов",
                from = "От АО Микрон • 3,2 тыс. участников",
                description = "За 48 часов ребята разрабатывают решения для реальных задач микроэлектроники.",
                tag = "#Кейсы",
                bannerRes = R.drawable.club_back2
            ),
            Club(
                id = "o3",
                title = "Клуб ИИ",
                from = "От АО Микрон • 3 тыс. участников",
                description = "Тренды, воркшопы по Python и ML, реальные задачи компании.",
                tag = "#ML",
                bannerRes = R.drawable.club_back3
            ),
            Club(
                id = "o4",
                title = "Клуб биоинженерии",
                from = "От НИИ БиоТех • 1,2 тыс. участников",
                description = "Клуб для студентов и молодых учёных, интересующихся генетикой и клеточными технологиями.",
                tag = "#Био",
                bannerRes = R.drawable.club_back4
            )
        )
    }

    // Данные клубов «От пользователей»
    val userClubs = remember {
        mutableStateListOf(
            Club(
                id = "u1",
                title = "Фуллстек-джедаи",
                from = "От пользователей • 4,5 тыс. участников",
                description = "Обмениваемся лайфхаками по Kotlin/JS/Go, делаем пет-проекты вместе.",
                tag = "#Dev",
                bannerRes = R.drawable.club_back2
            ),
            Club(
                id = "u2",
                title = "Дизайн &amp; Продукт",
                from = "От пользователей • 2,1 тыс. участников",
                description = "UI/UX, motion, JTBD и метрики — практикуемся и ревьюим кейсы.",
                tag = "#Design",
                bannerRes = R.drawable.club_back1
            )
        )
    }

    val currentList = if (selectedTab == 0) orgClubs else userClubs
    val filtered = if (query.isBlank()) currentList else currentList.filter {
        it.title.contains(query, ignoreCase = true) || it.tag.contains(query, ignoreCase = true)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgGrey)
            ) {
                item {
                    SubscriptionsHeader(
                        query = query,
                        onQueryChange = { query = it },
                        onAddClick = { showCreateDialog = true }
                    )
                }
                item { SubscriptionsTabs(selected = selectedTab, onSelect = { selectedTab = it }) }

                items(filtered, key = { it.id }) { club ->
                    ClubCard(club = club, onOpen = { /* TODO: переход в карточку клуба */ })
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }

            if (showCreateDialog) {
                CreateClubDialog(
                    onDismiss = { showCreateDialog = false },
                    onCreate = { isOrganization, yourName, entityName, activity, description ->
                        val id = "custom_${System.currentTimeMillis()}"
                        val tag = when {
                            activity.isNotBlank() -> "#${activity}"
                            isOrganization -> "#Организация"
                            else -> "#Клуб"
                        }
                        val fromText = if (isOrganization) {
                            "От ${entityName.ifBlank { yourName.ifBlank { "Организация" } }} • 0 участников"
                        } else {
                            "От пользователей • 0 участников"
                        }
                        val newClub = Club(
                            id = id,
                            title = entityName.ifBlank { "Без названия" },
                            from = fromText,
                            description = description.ifBlank { " " },
                            tag = tag,
                            bannerRes = null // без фотографии
                        )
                        if (isOrganization) {
                            orgClubs.add(0, newClub)
                            selectedTab = 0
                        } else {
                            userClubs.add(0, newClub)
                            selectedTab = 1
                        }
                        showCreateDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SubscriptionsHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(TopPanelPurple)
    ) {
        // Кнопка «+» справа сверху
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable { onAddClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Создать клуб")
        }

        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(
                text = "КЛУБЫ",
                modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold
            )
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Найти по тегу") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
private fun SubscriptionsTabs(selected: Int, onSelect: (Int) -> Unit) {
    val selectedColor = Color(0xFFDDEB87)
    val unselectedColor = Color(0xFFF1F2F6)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgGrey)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (selected == 0) selectedColor else unselectedColor)
                .clickable { onSelect(0) },
            contentAlignment = Alignment.Center
        ) { Text("От организаций", fontSize = 16.sp, fontWeight = if (selected == 0) FontWeight.SemiBold else FontWeight.Normal) }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (selected == 1) selectedColor else unselectedColor)
                .clickable { onSelect(1) },
            contentAlignment = Alignment.Center
        ) { Text("От пользователей", fontSize = 16.sp, fontWeight = if (selected == 1) FontWeight.SemiBold else FontWeight.Normal) }
    }
}

private data class Club(
    val id: String,
    val title: String,
    val from: String,
    val description: String,
    val tag: String,
    val bannerRes: Int? = null
)


@Composable
private fun ClubCard(
    club: Club,
    onOpen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Column {
            if (club.bannerRes != null) {
                Image(
                    painter = painterResource(id = club.bannerRes),
                    contentDescription = club.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(Color(0xFFEDEDED)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Без фото", color = Color(0xFF6B6B6B))
                }
            }

            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = club.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ButtonBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) { Text(club.tag, color = ButtonBlue, fontSize = 12.sp) }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = club.from, fontSize = 16.sp, color = Color(0xFF5C5C5C))
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = club.description, fontSize = 14.sp)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .clickable { onOpen() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Открыть", tint = Color.White)
                    }
                }
            }
        }
    }
}


@Composable
private fun CreateClubDialog(
    onDismiss: () -> Unit,
    onCreate: (isOrganization: Boolean, yourName: String, entityName: String, activity: String, description: String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 2.dp
        ) {
            var selected by remember { mutableStateOf(0) } // 0 – Клуб, 1 – Организация
            var yourName by remember { mutableStateOf("") }
            var entityName by remember { mutableStateOf("") }
            var activity by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }

            val isValid = entityName.isNotBlank()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .background(BgGrey)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(TopPanelPurple)
                ) {
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Text(
                            text = if (selected == 0) "СОЗДАТЬ СВОЙ КЛУБ" else "СОЗДАТЬ ОРГАНИЗАЦИЮ",
                            modifier = Modifier
                                .padding(start = 20.dp, bottom = 10.dp),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Подвкладки: Клуб / Организация
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgGrey)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val selectedColor = Color(0xFFDDEB87)
                    val unselectedColor = Color(0xFFF1F2F6)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected == 0) selectedColor else unselectedColor)
                            .clickable { selected = 0 },
                        contentAlignment = Alignment.Center
                    ) { Text("Клуб", fontSize = 16.sp, fontWeight = if (selected == 0) FontWeight.SemiBold else FontWeight.Normal) }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected == 1) selectedColor else unselectedColor)
                            .clickable { selected = 1 },
                        contentAlignment = Alignment.Center
                    ) { Text("Организация", fontSize = 16.sp, fontWeight = if (selected == 1) FontWeight.SemiBold else FontWeight.Normal) }
                }

                // Форма
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Введите имя", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = yourName,
                        onValueChange = { yourName = it },
                        placeholder = { Text("Ваше имя") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(if (selected == 1) "Название организации" else "Название клуба", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = entityName,
                        onValueChange = { entityName = it },
                        placeholder = { Text(if (selected == 1) "АО Микрон" else "Например, AI Club") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(if (selected == 1) "Сфера деятельности" else "Тематика клуба", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = activity,
                        onValueChange = { activity = it },
                        placeholder = { Text(if (selected == 1) "Микроэлектроника" else "ML / Data / Роботы") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(if (selected == 1) "Описание организации" else "Описание клуба", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Коротко о миссии") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onCreate(selected == 1, yourName.trim(), entityName.trim(), activity.trim(), description.trim())
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BgOrange)
                    ) {
                        Text("Создать", fontSize = 16.sp)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Отмена")
                    }
                }
            }
        }
    }
}


@Composable
fun Profile(navController: NavController) {
    val context = LocalContext.current
    val fullName by context.userDataStore.data.map { it[KEY_FULL_NAME] ?: "СОПРАНО АНАТОЛИЙ СЕРГЕЕВИЧ" }.collectAsState(initial = "СОПРАНО АНАТОЛИЙ СЕРГЕЕВИЧ")
    val phone by context.userDataStore.data.map { it[KEY_PHONE] ?: "" }.collectAsState(initial = "")
    val name = fullName.substringBefore(' ')
    val surname = fullName.substringAfter(' ', "")
    var university = "НИТУ МИСИС"
    var age = 20
    var aboutMe = "Призер олимпиады по физике"

    // Примеры тегов и XP (можешь менять)
    val tags = listOf("Python", "SQL", "Робототехника", "UI/UX", "ИИ")
    val xpList = listOf(
        "Python" to 0.9f,
        "SQL" to 0.75f,
        "Golang" to 0.5f,
        "Docker" to 0.65f
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // <- тут включаем скролл
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(TopPanelPurple)
            ) {
                Text(
                    text = "АНКЕТА",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 30.dp),
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgGrey)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 40.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.anketa),
                            contentDescription = "Первая картинка",
                            modifier = Modifier
                                .width(250.dp)
                                .height(140.dp)
                                .clickable {
                                    navController.navigate(Screen.Profile.route)
                                }
                                .clip(RoundedCornerShape(12.dp)
                                    )
                        )

                        Image(
                            painter = painterResource(id = R.drawable.education),
                            contentDescription = "Вторая картинка",
                            modifier = Modifier
                                .width(250.dp)
                                .height(140.dp)
                                .clickable {
                                    navController.navigate(Screen.Education.route)
                                }
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp, start = 20.dp, end = 20.dp)
                            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(bottom = 20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.avatar),
                                    contentDescription = "Аватарка",
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(185.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = "Образование", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text(text = university, fontSize = 15.sp, fontWeight = FontWeight.Normal)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "Возраст", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text(text = age.toString(), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "Опыт", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text(text = aboutMe, fontSize = 15.sp, fontWeight = FontWeight.Normal)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "Телефон", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text(text = phone, fontSize = 15.sp, fontWeight = FontWeight.Normal)
                                }
                            }


                            Text(
                                text = name,
                                fontSize = 28.sp,
                                modifier = Modifier.padding(start = 20.dp, top = 12.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = surname,
                                fontSize = 28.sp,
                                modifier = Modifier.padding(start = 20.dp, top = 3.dp),
                                fontWeight = FontWeight.Bold
                            )


                            Text(
                                text = "// КОМПЕТЕНЦИИ",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 20.dp, top = 14.dp)
                            )


                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                tags.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .height(34.dp)
                                            .border(
                                                width = 1.dp,
                                                color = Color.Black,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color.Transparent)
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = tag, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }


                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "// XP",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 20.dp, top = 8.dp)
                            )

                            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp)) {
                                xpList.forEach { (label, progress) ->
                                    Column(modifier = Modifier.padding(vertical = 8.dp)) {

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                            Text(text = "${(progress * 100).toInt()}%", fontSize = 14.sp)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))


                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(12.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE0E0E0))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(progress)
                                                    .height(12.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(TopPanelPurple)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Education(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(TopPanelPurple)
            ) {
                Text(
                    text = "ОБРАЗОВАНИЕ",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 30.dp),
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgGrey)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 40.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.anketa),
                            contentDescription = "Первая картинка",
                            modifier = Modifier
                                .width(250.dp)
                                .height(140.dp)
                                .clickable {
                                    navController.navigate(Screen.Profile.route)
                                }
                                .clip(RoundedCornerShape(12.dp)
                                )
                        )

                        Image(
                            painter = painterResource(id = R.drawable.education),
                            contentDescription = "Вторая картинка",
                            modifier = Modifier
                                .width(250.dp)
                                .height(140.dp)
                                .clickable {
                                    navController.navigate(Screen.Education.route)
                                }
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp, start = 20.dp, end = 20.dp)
                            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(bottom = 20.dp)
                    ) {
                        Column {
                            Text(
                                text = "// ПРОЙДЕННЫЕ КУРСЫ",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 20.dp, top = 14.dp)
                            )

                            Row (Modifier.padding(top=5.dp)){
                                Image(
                                    painter = painterResource(id = R.drawable.edpic1),
                                    contentDescription = "Первая картинка",
                                    modifier = Modifier
                                        .width(180.dp)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.edpic2),
                                    contentDescription = "Первая картинка",
                                    modifier = Modifier
                                        .width(180.dp)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }

                            Row (Modifier.padding(top=5.dp)){
                                Image(
                                    painter = painterResource(id = R.drawable.edpic3),
                                    contentDescription = "Первая картинка",
                                    modifier = Modifier
                                        .width(180.dp)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.edpic4),
                                    contentDescription = "Первая картинка",
                                    modifier = Modifier
                                        .width(180.dp)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }

                            Text(
                                text = "// ХОЧУ ИЗУЧИТЬ",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 20.dp, top = 14.dp)
                            )

                            Row (Modifier.padding(top=5.dp)
                                .align(Alignment.CenterHorizontally),
                                ){
                                Image(
                                    painter = painterResource(id = R.drawable.edpic21),
                                    contentDescription = "Первая картинка",
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .padding(end=10.dp)
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.edpic22),
                                    contentDescription = "Первая картинка",
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .padding(end=10.dp)
                                )

                            }

                        }


                    }
                }
            }
        }
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavHostController) {
    val context = LocalContext.current
    var fullName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val isValid = fullName.isNotBlank() && phone.count { it.isDigit() } >= 10 && password.length >= 6

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Регистрация") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                singleLine = true,
                label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                singleLine = true,
                label = { Text("Номер телефона") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                label = { Text("Пароль") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        // Save to DataStore (not saving password here for security)
                        context.userDataStore.edit { prefs ->
                            prefs[KEY_FULL_NAME] = fullName
                            prefs[KEY_PHONE] = phone
                        }
                        // Go to profile after successful registration
                        navController.popBackStack()
                        navController.navigate(Screen.Profile.route)
                    }
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BgOrange)
            ) {
                Text("Зарегистрироваться", fontSize = 16.sp)
            }
        }
    }
}

private data class Community(
    val id: String,
    val name: String,
    val followers: String,
    val avatarRes: Int,
    val isFollowing: Boolean
)

@Composable
private fun CommunityCard(
    community: Community,
    onToggleFollow: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = community.avatarRes),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = community.name, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = community.followers, fontSize = 14.sp, color = Color(0xFF7A7A7A))
            }

            if (community.isFollowing) {
                Button(
                    onClick = onToggleFollow,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BgOrange),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Following")
                }
            } else {
                OutlinedButton(
                    onClick = onToggleFollow,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ButtonBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonBlue),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Follow")
                }
            }
        }
    }
}
