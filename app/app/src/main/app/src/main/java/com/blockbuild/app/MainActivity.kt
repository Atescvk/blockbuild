package com.blockbuild.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BlockBuildApp()
        }
    }
}

data class ImportedFile(
    val name: String,
    val uri: Uri,
    val size: Long
)

@Composable
fun BlockBuildApp() {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    val importedFiles = remember {
        mutableStateListOf<ImportedFile>()
    }

    var message by remember {
        mutableStateOf("Henüz dünya veya paket eklenmedi.")
    }

    val context = LocalContext.current

    val filePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                val file = DocumentFile.fromSingleUri(
                    context,
                    uri
                )

                val name =
                    file?.name ?: "Bilinmeyen dosya"

                val lowerName =
                    name.lowercase()

                if (
                    lowerName.endsWith(".mcworld") ||
                    lowerName.endsWith(".mcpack") ||
                    lowerName.endsWith(".mcaddon") ||
                    lowerName.endsWith(".zip")
                ) {

                    importedFiles.add(
                        ImportedFile(
                            name = name,
                            uri = uri,
                            size = file?.length() ?: 0L
                        )
                    )

                    message =
                        "$name BlockBuild'e eklendi."

                } else {

                    message =
                        "Desteklenen dosyalar: .mcworld, .mcpack, .mcaddon"
                }
            }
        }

    MaterialTheme {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            Scaffold(

                topBar = {

                    TopAppBar(

                        title = {
                            Text("BlockBuild")
                        },

                        actions = {

                            IconButton(
                                onClick = {
                                    message =
                                        "BlockBuild Phase 1"
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Bilgi"
                                )
                            }
                        }
                    )
                },

                bottomBar = {

                    NavigationBar {

                        NavigationBarItem(

                            selected = selectedTab == 0,

                            onClick = {
                                selectedTab = 0
                            },

                            icon = {
                                Icon(
                                    Icons.Default.FolderOpen,
                                    contentDescription = null
                                )
                            },

                            label = {
                                Text("Dosyalar")
                            }
                        )

                        NavigationBarItem(

                            selected = selectedTab == 1,

                            onClick = {
                                selectedTab = 1
                            },

                            icon = {
                                Icon(
                                    Icons.Default.Archive,
                                    contentDescription = null
                                )
                            },

                            label = {
                                Text("Yapılar")
                            }
                        )

                        NavigationBarItem(

                            selected = selectedTab == 2,

                            onClick = {
                                selectedTab = 2
                            },

                            icon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null
                                )
                            },

                            label = {
                                Text("Ayarlar")
                            }
                        )
                    }
                },

                floatingActionButton = {

                    FloatingActionButton(

                        onClick = {

                            filePicker.launch(
                                arrayOf(
                                    "*/*"
                                )
                            )
                        }
                    ) {

                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Dosya ekle"
                        )
                    }
                }

            ) { paddingValues ->

                when (selectedTab) {

                    0 -> {

                        FilesScreen(
                            modifier =
                                Modifier.padding(
                                    paddingValues
                                ),
                            importedFiles =
                                importedFiles,
                            message =
                                message,
                            onDelete = {
                                importedFiles.remove(it)
                            },
                            onPick = {

                                filePicker.launch(
                                    arrayOf("*/*")
                                )
                            }
                        )
                    }

                    1 -> {

                        StructuresScreen(
                            modifier =
                                Modifier.padding(
                                    paddingValues
                                )
                        )
                    }

                    2 -> {

                        SettingsScreen(
                            modifier =
                                Modifier.padding(
                                    paddingValues
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilesScreen(
    modifier: Modifier,
    importedFiles: List<ImportedFile>,
    message: String,
    onDelete: (ImportedFile) -> Unit,
    onPick: () -> Unit
) {

    Column(
        modifier = modifier.padding(16.dp)
    ) {

        Text(
            text = "Minecraft Dosyaları",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (importedFiles.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        Icons.Default.FolderOpen,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text("Henüz dosya yok")

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Button(
                        onClick = onPick
                    ) {

                        Text(
                            "MCWORLD / MCPACK Ekle"
                        )
                    }
                }
            }

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    importedFiles,
                    key = {
                        it.uri.toString()
                    }
                ) { item ->

                    FileCard(
                        item = item,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

@Composable
fun FileCard(
    item: ImportedFile,
    onDelete: (ImportedFile) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier =
                Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                Icons.Default.Archive,
                contentDescription = null
            )

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
            ) {

                Text(
                    text = item.name,
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Text(
                    text = formatSize(item.size),
                    style =
                        MaterialTheme.typography.bodySmall
                )
            }

            IconButton(
                onClick = {
                    onDelete(item)
                }
            ) {

                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Sil"
                )
            }
        }
    }
}

@Composable
fun StructuresScreen(
    modifier: Modifier
) {

    Column(
        modifier = modifier.padding(16.dp)
    ) {

        Text(
            text = "Yapı Kütüphanesi",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    "Yapı sistemi sonraki fazlarda eklenecek."
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedButton(
                    onClick = {}
                ) {

                    Text("Yakında")
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    modifier: Modifier
) {

    Column(
        modifier = modifier.padding(16.dp)
    ) {

        Text(
            text = "Ayarlar",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text("BlockBuild 0.1.0")

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            "Minecraft Bedrock dünya ve yapı araçları."
        )
    }
}

fun formatSize(
    bytes: Long
): String {

    if (bytes <= 0) {
        return "Boyut bilinmiyor"
    }

    val kb =
        bytes / 1024.0

    if (kb < 1024) {

        return "${
            DecimalFormat("0.0").format(kb)
        } KB"
    }

    return "${
        DecimalFormat("0.00").format(
            kb / 1024.0
        )
    } MB"
}
