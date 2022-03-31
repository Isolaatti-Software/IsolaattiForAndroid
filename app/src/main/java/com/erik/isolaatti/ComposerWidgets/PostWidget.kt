package com.erik.isolaatti.ComposerWidgets

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText

import com.erik.isolaatti.classes.Post
import com.halilibo.richtext.ui.ListStyle
import com.halilibo.richtext.ui.RichTextStyle

@Composable()
fun Post(post: Post) {
    Card (modifier = Modifier.padding(8.dp)
        .then(Modifier.fillMaxWidth()),
        backgroundColor = MaterialTheme.colors.background
    ){
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                ClickableText(modifier = Modifier.padding(16.dp),
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    text = AnnotatedString(post.postData.username),
                    onClick = {
                        Log.println(Log.INFO,"ISOLAATTI", "Nombre del usuario")
                    }
                )
                IconButton(
                    onClick = {},
                    content = { Icon(Icons.Filled.OpenInNew,null) }
                )
            }
            Row {
                IconButton(
                    onClick = {},
                    content = {
                        Icon(Icons.Filled.PlayArrow,null)
                    }
                )
            }
            Box(modifier = Modifier.heightIn(Dp.Unspecified,500.dp).then(Modifier.padding(16.dp))) {
                RichText(modifier = Modifier.wrapContentSize()){
                    Markdown(post.postData.content)
                }
            }

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {},
                    modifier = Modifier.padding(8.dp),
                    border = null,
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                    colors = ButtonDefaults.textButtonColors(),
                    content = {
                        Icon(Icons.Filled.ThumbUp,null,
                            tint = if(post.postData.liked){
                                MaterialTheme.colors.primary
                            }
                            else {
                                MaterialTheme.colors.onBackground
                            })
                        Text(post.postData.numberOfLikes.toString(),
                            color = if(post.postData.liked){
                                MaterialTheme.colors.primary
                            }
                            else {
                                MaterialTheme.colors.onBackground
                            }
                        )
                    }
                )
            }
        }
    }
}