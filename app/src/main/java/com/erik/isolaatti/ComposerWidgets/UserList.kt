package com.erik.isolaatti.ComposerWidgets

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*

import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.erik.isolaatti.activities.MyProfile

import com.erik.isolaatti.classes.UserDataOnFollowingLists

@OptIn(ExperimentalMaterialApi::class)
@Composable()
fun UserListItem(profileData: UserDataOnFollowingLists){
    val context = LocalContext.current
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).then(Modifier.fillMaxWidth()),
        shape = MaterialTheme.shapes.medium,

        onClick = {
            val intent = Intent(context,MyProfile::class.java)
            intent.putExtra("userId",profileData.id)
            context.startActivity(intent)
        }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context).data(data = profileData.imageUrl)
                            .apply(block = fun ImageRequest.Builder.() {
                                transformations(CircleCropTransformation())
                            }).build()
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                        .then(Modifier.align(alignment = Alignment.CenterVertically))
                )
                Text(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    fontWeight = FontWeight(300),
                    text = profileData.name
                )
            }
        }
    }
}
