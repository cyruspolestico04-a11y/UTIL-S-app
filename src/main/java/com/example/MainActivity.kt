package com.example

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

val BgColor = Color(0xFFFDFBFF)
val TextDark = Color(0xFF1A1C1E)
val PrimaryBlue = Color(0xFF0061A4)
val DarkBlueText = Color(0xFF001D34)
val LightBlueCard = Color(0xFFD1E4FF)
val SecondaryBlueText = Color(0xFF004881)
val StatsBg = Color(0xFFF2F3F7)
val BorderLight = Color(0xFFE0E2EC)
val StatsText = Color(0xFF44474E)
val RedText = Color(0xFFBA1A1A)
val PurpleIconBg = Color(0xFFE8DEF8)
val DarkPurple = Color(0xFF21005D)
val CyanIconBg = Color(0xFFD0F0FD)
val DarkCyan = Color(0xFF003543)
val YellowIconBg = Color(0xFFFFE082)
val DarkYellow = Color(0xFF332D00)
val PinkIconBg = Color(0xFFF9DEDC)
val DarkRed = Color(0xFF410E0B)
val BorderMedium = Color(0xFFC4C6CF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DeviceUtilsApp()
            }
        }
    }
}

@Composable
fun DeviceUtilsApp() {
    var currentScreen by remember { mutableStateOf("dashboard") }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            if (currentScreen == "dashboard") {
                NavigationBar(
                    containerColor = Color.White,
                    modifier = Modifier.border(width = 1.dp, color = BorderMedium)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            if (selectedTab == 0) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(LightBlueCard)
                                        .padding(horizontal = 20.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = "Security", tint = DarkBlueText)
                                }
                            } else {
                                Icon(Icons.Default.Security, contentDescription = "Security", tint = StatsText)
                            }
                        },
                        label = { Text("Security", color = if (selectedTab == 0) DarkBlueText else StatsText, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Files", tint = StatsText) },
                        label = { Text("Files", color = StatsText, fontWeight = FontWeight.Medium) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Widgets, contentDescription = "Tools", tint = StatsText) },
                        label = { Text("Tools", color = StatsText, fontWeight = FontWeight.Medium) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Report", tint = StatsText) },
                        label = { Text("Report", color = StatsText, fontWeight = FontWeight.Medium) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(BgColor)
        ) {
            when (currentScreen) {
                "dashboard" -> DashboardScreen(
                    onNavigateToAppManager = { currentScreen = "appManager" },
                    onNavigateToStorage = { currentScreen = "storage" }
                )
                "appManager" -> AppManagerScreen(onBack = { currentScreen = "dashboard" })
                "storage" -> StorageScanScreen(onBack = { currentScreen = "dashboard" })
            }
        }
    }
}

@Composable
fun DashboardScreen(onNavigateToAppManager: () -> Unit, onNavigateToStorage: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Text("UTIL'S", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = DarkBlueText, letterSpacing = (-0.5).sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.History, contentDescription = "History", tint = Color.Gray)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Gray)
                }
            }
        }

        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(LightBlueCard)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Animated Pulse Circle
                    val infiniteTransition = rememberInfiniteTransition()
                    val pulseAnim by infiniteTransition.animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(width = 4.dp, color = Color.White, shape = CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("System Secure", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = DarkBlueText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Last full scan: 14 minutes ago", fontSize = 14.sp, color = SecondaryBlueText, modifier = Modifier.padding(bottom = 16.dp))
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.White),
                        shape = CircleShape,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Full System Scan", fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                    }
                }
            }

            // High Density Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), title = "Apps", value = "128", status = "Healthy", statusColor = Color(0xFF16A34A))
                StatCard(modifier = Modifier.weight(1f), title = "Files", value = "4.2K", status = "0 Threats", statusColor = Color.Gray)
                StatCard(modifier = Modifier.weight(1f), title = "Risk", value = "Low", valueColor = RedText, status = "Secured", statusColor = Color.Gray)
            }

            // Management Grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ManagementCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Apps,
                        iconBg = PurpleIconBg,
                        iconTint = DarkPurple,
                        title = "Software Manager",
                        subtitle = "Detect & Update installed tools",
                        onClick = onNavigateToAppManager
                    )
                    ManagementCard(
                        modifier = Modifier.weight(1f),
                        icon = painterResource(id = android.R.drawable.stat_sys_warning) , // Using appropriate vector image.
                        iconBg = CyanIconBg,
                        iconTint = DarkCyan,
                        title = "File Guard",
                        subtitle = "Scan archives & suspicious files",
                        useVector = false, 
                        // Fallback
                    )
                }
                Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ManagementCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Key,
                        iconBg = YellowIconBg,
                        iconTint = DarkYellow,
                        title = "Auto Uninstaller",
                        subtitle = "Remove risky apps automatically",
                        onClick = onNavigateToAppManager
                    )
                    ManagementCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.DeleteSweep,
                        iconBg = PinkIconBg,
                        iconTint = DarkRed,
                        title = "Storage Optimization",
                        subtitle = "Clean temporary & residual data",
                        onClick = onNavigateToStorage
                    )
                }
            }

            // Real-time Banner
            var showBanner by remember { mutableStateOf(true) }
            if (showBanner) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(StatsBg)
                        .border(width = 1.dp, color = BorderLight, shape = RoundedCornerShape(16.dp))
                        .padding(start = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(4.dp).height(48.dp).background(PrimaryBlue))
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Real-time protection is monitoring 4 active processes.", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextDark, modifier = Modifier.weight(1f))
                    IconButton(onClick = { showBanner = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, valueColor: Color = DarkBlueText, status: String, statusColor: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(StatsBg)
            .border(width = 1.dp, color = BorderLight, shape = RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatsText, letterSpacing = 1.sp)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = valueColor, modifier = Modifier.padding(vertical = 4.dp))
        Text(status, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = statusColor)
    }
}

@Composable
fun ManagementCard(
    modifier: Modifier = Modifier,
    icon: Any,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
    useVector: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(width = 1.dp, color = BorderMedium, shape = RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            if (icon is ImageVector) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            } else if (icon is androidx.compose.ui.graphics.painter.Painter) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
        }
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1B1F))
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF49454F), lineHeight = 14.sp)
        }
    }
}

data class AppItem(val name: String, val packageName: String, val icon: Drawable?)
fun Drawable.toBitmapSafely(): Bitmap {
    if (this is BitmapDrawable && this.bitmap != null) {
        return this.bitmap
    }
    val width = if (intrinsicWidth > 0) intrinsicWidth else 144
    val height = if (intrinsicHeight > 0) intrinsicHeight else 144
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppManagerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<AppItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val userApps = packages.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            val mappedApps = userApps.map {
                AppItem(
                    name = pm.getApplicationLabel(it).toString(),
                    packageName = it.packageName,
                    icon = pm.getApplicationIcon(it)
                )
            }.sortedBy { it.name.lowercase() }
            apps = mappedApps
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Software Manager") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgColor)
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                item {
                    Text(
                        text = "User Installed Apps (${apps.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(apps, key = { it.packageName }) { app ->
                    ListItem(
                        headlineContent = { Text(app.name) },
                        supportingContent = { Text(app.packageName) },
                        leadingContent = {
                            app.icon?.let { drawable ->
                                val bitmap = remember(drawable) { drawable.toBitmapSafely().asImageBitmap() }
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        },
                        trailingContent = {
                            IconButton(onClick = {
                                val intent = Intent(Intent.ACTION_DELETE).apply {
                                    data = Uri.parse("package:${app.packageName}")
                                }
                                context.startActivity(intent)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Uninstall ${app.name}", tint = RedText)
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = BgColor)
                    )
                    HorizontalDivider(color = BorderLight)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScanScreen(onBack: () -> Unit) {
    var scanning by remember { mutableStateOf(false) }
    var scanComplete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storage Optimization") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (scanComplete) Icons.Default.CheckCircle else Icons.Default.DeleteSweep,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (scanComplete) "Storage Check Complete" else "Analyze Storage Health",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = DarkBlueText
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (scanning) {
                CircularProgressIndicator(color = PrimaryBlue)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Checking for optimal storage usage...", style = MaterialTheme.typography.bodyLarge, color = TextDark)

                LaunchedEffect(Unit) {
                    delay(2500)
                    scanning = false
                    scanComplete = true
                }
            } else {
                if (scanComplete) {
                    Text(
                        text = "No critical storage issues found. Your device looks clean.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = StatsText
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                Button(
                    onClick = {
                        scanning = true
                        scanComplete = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(if (scanComplete) "Scan Again" else "Start Scan")
                }
            }
        }
    }
}

