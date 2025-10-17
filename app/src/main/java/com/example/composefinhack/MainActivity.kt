package com.example.composefinhack

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.example.composefinhack.ui.theme.ButtonBlue


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WelcomeScreen()
        }
    }
}

//@Preview
@Composable
fun WelcomeScreen() {
    var loginText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(37.dp, 35.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),

        ) {
            Image(
                painter = painterResource(id = R.drawable.vk_logo_black___white),
                contentDescription = "logo",
                modifier = Modifier.size(140.dp)
                    .padding(top = 52.dp)
            )

            Text( modifier = Modifier.padding(top = 44.dp),
                text = "Finance\nTracker",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=44.dp)
            ) {
                OutlinedTextField(
                    value = loginText,
                    onValueChange = { loginText = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    ),
                    label = { Text("Логин") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = passwordText,
                    onValueChange = { passwordText = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp
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
                        TODO("переход на восст")
                    },
                    modifier = Modifier.align(Alignment.End),
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF1E88E5),
                        textDecoration = TextDecoration.Underline
                    )
                )

                Button(
                    onClick = { /* TODO: handle click */ },
                    modifier = Modifier
                        .padding(top = 50.dp)
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
                ) { Text("Войти", fontSize = 17.sp) }


                Text( modifier = Modifier
                    .padding(top = 44.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                    text = "или",
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )

                ClickableText(
                    text = AnnotatedString("Зарегистрироваться"),
                    onClick = {
                        TODO("переход на регу")
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 44.dp),
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF1E88E5),
                        textDecoration = TextDecoration.Underline
                    )
                )

            }



        }
    }
}

@Preview
@Composable
fun MainScreen(){
    
}




