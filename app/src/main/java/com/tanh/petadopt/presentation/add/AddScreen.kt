package com.tanh.petadopt.presentation.add

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.ui.theme.Gray
import com.tanh.petadopt.ui.theme.PetAdoptTheme
import com.tanh.petadopt.ui.theme.Yellow100
import com.tanh.petadopt.ui.theme.Yellow60
import com.tanh.petadopt.util.Util

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    viewModel: AddViewModel? = null,
    onNavigate: (OneTimeEvent.Navigate) -> Unit
) {

    val state = viewModel?.state?.collectAsState(initial = AddUiState())?.value ?: AddUiState()

    val context = LocalContext.current

    val isNameError = state.nameError == null
    val isBreedError = state.breedError == null
    val isAgeError = state.ageError == null
    val isWeightError = state.weightError == null
    val isAddressError = state.addressError == null
    val isAboutError = state.addressError == null

    var genderExpanded by remember {
        mutableStateOf(false)
    }

    var genderSelected by remember {
        mutableStateOf("Male")
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var selectedOption by remember {
        mutableStateOf("Dogs")
    }

    var inputName by remember {
        mutableStateOf("")
    }

    var inputBreed by remember {
        mutableStateOf("")
    }

    var inputAge by remember {
        mutableStateOf("")
    }

    var inputWeight by remember {
        mutableStateOf("")
    }

    var inputAddress by remember {
        mutableStateOf("")
    }

    var inputAbout by remember {
        mutableStateOf("")
    }

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel?.uploadImageToAzureStorage(uri = uri)
            } else {
                Log.d("addscreen", "not found uri")
            }
        }


    LaunchedEffect(true) {
        viewModel?.chanel?.collect { event ->
            when (event) {
                is OneTimeEvent.Navigate -> {
                    onNavigate(event)
                }

                is OneTimeEvent.ShowSnackbar -> TODO()
                is OneTimeEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel?.onNavToHome()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Add New Pet",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .weight(1f)
            )
        }

        //photo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            //Chọn ảnh
            Text(
                text = "Add New Pet for adaption",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            AsyncImage(
                model = state.uri.ifBlank { Util.CAT_PAW_URL },
                contentDescription = "Add new pet",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        color = Gray,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
            )

            //Chọn tên
            Text(
                text = if (isNameError) "Pet Name *"
                else "Pet Name *  ${state.nameError}",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = if (isNameError) Color.Black else Color.Red,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            TextField(
                value = inputName,
                onValueChange = { it ->
                    inputName = it
                    viewModel?.onNameChange(it)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (isNameError) Color.White else Color.Red
                    )
                    .onFocusChanged { focusState ->
                        if(focusState.isFocused && !isNameError) {
                            viewModel?.resetNameState()
                        }
                    },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    containerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.padding(4.dp))
            //Chọn category
            Text(
                text = "Pet Category *",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = {
                    categoryExpanded = !categoryExpanded
                }
            ) {
                TextField(
                    value = TextFieldValue(selectedOption),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    Util.petCategory.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(text = option)
                            },
                            onClick = {
                                selectedOption = option
                                viewModel?.onCategorySelect(option)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(4.dp))
            //Chọn breed
            Text(
                text = if (isBreedError) "Breed *"
                else "Breed *  ${state.nameError}",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = if (isBreedError) Color.Black else Color.Red,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            TextField(
                value = inputBreed,
                onValueChange = { it ->
                    inputBreed = it
                    viewModel?.onBreedChange(it)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (isBreedError) Color.White else Color.Red
                    )
                    .onFocusChanged { focusState ->
                        if(focusState.isFocused && !isBreedError) {
                            viewModel?.resetBreedState()
                        }
                    },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    containerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.padding(4.dp))
            //Nhập age
            Text(
                text = if (isAgeError) "Age *"
                else "Age *  ${state.ageError}",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = if (isAgeError) Color.Black else Color.Red,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            TextField(
                value = inputAge,
                onValueChange = {
                    inputAge = it
                    viewModel?.onAgeChange(it)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (isAgeError) Color.White else Color.Red
                    )
                    .onFocusChanged { focusState ->
                        if(focusState.isFocused && !isAgeError) {
                            viewModel?.resetAgeState()
                        }
                    },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.padding(4.dp))
            //Chọn gender
            Text(
                text = "Gender *",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = {
                    genderExpanded = !genderExpanded
                }
            ) {
                TextField(
                    value = genderSelected,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = {
                        genderExpanded = false
                    }
                ) {
                    Util.gender.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option
                                )
                            },
                            onClick = {
                                genderSelected = option
                                if(genderSelected == "Male") {
                                    viewModel?.onGenderChange(true)
                                } else {
                                    viewModel?.onGenderChange(false)
                                }
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(4.dp))
            //Nhập weight
            Text(
                text = if (isWeightError) "Weight *"
                else "Weight *  ${state.weightError}",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = if (isWeightError) Color.Black else Color.Red,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            TextField(
                value = inputWeight,
                onValueChange = {
                    inputWeight = it
                    viewModel?.onWeightChange(it)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (isWeightError) Color.White else Color.Red
                    )
                    .onFocusChanged { focusState ->
                        if(focusState.isFocused && !isWeightError) {
                            viewModel?.resetWeightState()
                        }
                    },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.padding(4.dp))
            //Chọn address
            Text(
                text = if (isAddressError) "Address *"
                else "Address *  ${state.addressError}",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = if (isAddressError) Color.Black else Color.Red,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            TextField(
                value = inputAddress,
                onValueChange = { it ->
                    inputAddress = it
                    viewModel?.onAddressChange(it)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (isAddressError) Color.White else Color.Red
                    )
                    .onFocusChanged { focusState ->
                        if(focusState.isFocused && !isAddressError) {
                            viewModel?.resetAddressState()
                        }
                    },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    containerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.padding(4.dp))
            //Chọn about
            Text(
                text = if (isAboutError) "About *"
                else "About *  ${state.aboutError}",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = if (isAboutError) Color.Black else Color.Red,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            TextField(
                value = inputAbout,
                onValueChange = { it ->
                    inputAbout = it
                    viewModel?.onAboutChange(it)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (isAboutError) Color.White else Color.Red
                    )
                    .onFocusChanged { focusState ->
                        if(focusState.isFocused && !isAboutError) {
                            viewModel?.resetAboutState()
                        }
                    },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    containerColor = Color.White
                )
            )

            //button
            Spacer(modifier = Modifier.padding(8.dp))
            Button(
                onClick = {
                    viewModel?.onInsertPet()
                },
                colors = ButtonColors(
                    containerColor = Yellow60,
                    contentColor = Color.Black,
                    disabledContainerColor = Yellow60,
                    disabledContentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Submit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun PreviewAddScreen(modifier: Modifier = Modifier) {
    PetAdoptTheme {
        AddScreen { }
    }
}