package `in`.chess.scholars.global.presentation.rules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private data class Rule(val title: String, val description: String)
private data class RuleCategory(val title: String, val icon: ImageVector, val rules: List<Rule>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessRulesScreen(navController: NavController) {
    val ruleCategories = remember { getChessRuleCategories() }
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chess Rules", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1a1a2e),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF16213e)
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex,
                containerColor = Color(0xFF1a1a2e),
                edgePadding = 16.dp
            ) {
                ruleCategories.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = { Text(category.title) },
                        icon = { Icon(category.icon, contentDescription = category.title) }
                    )
                }
            }

            RuleCategoryContent(category = ruleCategories[selectedCategoryIndex])
        }
    }
}

@Composable
private fun RuleCategoryContent(category: RuleCategory) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF16213e),
                        Color(0xFF0f3460)
                    )
                )
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(category.rules) { rule ->
            RuleCard(rule = rule)
        }
    }
}

@Composable
private fun RuleCard(rule: Rule) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e).copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = rule.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = Color.White
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = slideInVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Text(
                    text = rule.description,
                    modifier = Modifier.padding(top = 12.dp),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

private fun getChessRuleCategories(): List<RuleCategory> {
    return listOf(
        RuleCategory("Basics", Icons.Default.School, listOf(
            Rule("The Goal", "The objective is to checkmate the opponent's king. This happens when the king is in a position to be captured (in check) and cannot escape capture."),
            Rule("The Board", "The game is played on an 8x8 grid of 64 squares. The board is set up with a light square on each player's bottom-right corner."),
            Rule("The Pieces", "Each player starts with 16 pieces: one king, one queen, two rooks, two knights, two bishops, and eight pawns.")
        )),
        RuleCategory("Pieces", Icons.Default.Games, listOf(
            Rule("King", "Moves one square in any direction. The king is the most important piece and must be protected."),
            Rule("Queen", "Moves any number of squares along a rank, file, or diagonal. It's the most powerful piece."),
            Rule("Rook", "Moves any number of squares along a rank or file."),
            Rule("Bishop", "Moves any number of squares diagonally."),
            Rule("Knight", "Moves in an 'L' shape: two squares in one direction and then one square perpendicular. It's the only piece that can jump over other pieces."),
            Rule("Pawn", "Moves forward one square, but on its first move, it can move two squares. Pawns capture diagonally one square forward.")
        )),
        RuleCategory("Special", Icons.Default.Star, listOf(
            Rule("Castling", "A special move involving the king and one of the rooks. It's the only move where a player moves two pieces at once. The king moves two squares towards a rook, and the rook moves to the square the king crossed."),
            Rule("En Passant", "A special pawn capture that can only occur immediately after a pawn makes a two-square move from its starting square, and it could have been captured by an enemy pawn had it moved only one square."),
            Rule("Pawn Promotion", "When a pawn reaches the other side of the board, it can be promoted to any other piece (queen, rook, bishop, or knight).")
        )),
        RuleCategory("Draws", Icons.Default.Handshake, listOf(
            Rule("Stalemate", "If a player is not in check but has no legal moves, the game is a draw by stalemate."),
            Rule("Threefold Repetition", "If the same board position occurs three times with the same player to move, a player can claim a draw."),
            Rule("50-Move Rule", "If 50 consecutive moves have been made by each player without a pawn move or a capture, a player can claim a draw."),
            Rule("Insufficient Material", "The game is drawn if neither player has enough pieces to force a checkmate (e.g., king vs. king).")
        ))
    )
}
