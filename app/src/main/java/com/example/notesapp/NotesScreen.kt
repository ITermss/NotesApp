
package com.example.notesapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    vm: NotesViewModel = viewModel()
) {
    val notes by vm.notes.collectAsState()

    // State form tambah
    var inputTitle by rememberSaveable {
        mutableStateOf("")
    }

    var inputNote by rememberSaveable {
        mutableStateOf("")
    }

    // State form edit
    var editingId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var editingTitle by rememberSaveable {
        mutableStateOf("")
    }

    var editingNote by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // JUDUL APLIKASI
        Text(
            text = "Catatan Saya",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        // FORM INPUT TITLE
        OutlinedTextField(
            value = inputTitle,
            onValueChange = { inputTitle = it },
            label = { Text("Title") },
            placeholder = { Text("Masukkan judul catatan") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        // FORM INPUT NOTE
        OutlinedTextField(
            value = inputNote,
            onValueChange = { inputNote = it },
            label = { Text("Note") },
            placeholder = { Text("Masukkan isi catatan") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(Modifier.height(8.dp))

        // TOMBOL TAMBAH
        Button(
            onClick = {
                vm.addNote(inputTitle, inputNote)

                inputTitle = ""
                inputNote = ""
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = inputTitle.isNotBlank() &&
                    inputNote.isNotBlank()
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "Tambah"
            )

            Spacer(Modifier.width(8.dp))

            Text("Tambah Catatan")
        }

        Spacer(Modifier.height(16.dp))

        // JUMLAH CATATAN
        Text(
            text = "Jumlah catatan: ${notes.size}",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(8.dp))

        // DAFTAR CATATAN
        if (notes.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada catatan",
                    color = Color.Gray
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(notes, key = { it.id }) { note ->

                    if (editingId == note.id) {

                        // MODE EDIT
                        NoteItemEditing(
                            title = editingTitle,
                            note = editingNote,

                            onTitleChange = {
                                editingTitle = it
                            },

                            onNoteChange = {
                                editingNote = it
                            },

                            onSave = {
                                vm.updateNote(
                                    note,
                                    editingTitle,
                                    editingNote
                                )

                                editingId = null
                            },

                            onCancel = {
                                editingId = null
                            }
                        )

                    } else {

                        // MODE TAMPILAN BIASA
                        NoteItem(
                            note = note,

                            onEdit = {
                                editingId = note.id
                                editingTitle = note.title
                                editingNote = note.note
                            },

                            onDelete = {
                                vm.deleteNote(note)
                            }
                        )
                    }

                    HorizontalDivider()
                }
            }
        }
    }
}


// ITEM CATATAN
@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    ListItem(
        headlineContent = {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold
            )
        },

        supportingContent = {
            Column {
                Text(text = note.note)

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "ID: ${note.id}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },

        trailingContent = {
            Row {

                IconButton(onClick = onEdit) {
                    Icon(
                        painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = Color(0xFF2196F3)
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        painterResource(R.drawable.ic_delete),
                        contentDescription = "Hapus",
                        tint = Color(0xFFF44336)
                    )
                }
            }
        }
    )
}


// FORM EDIT CATATAN
@Composable
fun NoteItemEditing(
    title: String,
    note: String,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        // INPUT TITLE
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Edit Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        // INPUT NOTE
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Edit Note") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        // TOMBOL SIMPAN DAN BATAL
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            IconButton(onClick = onSave) {
                Icon(
                    painterResource(R.drawable.ic_check),
                    contentDescription = "Simpan",
                    tint = Color(0xFF4CAF50)
                )
            }

            IconButton(onClick = onCancel) {
                Icon(
                    painterResource(R.drawable.ic_close),
                    contentDescription = "Batal"
                )
            }
        }
    }
}
