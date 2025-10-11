package com.uitopic.restockmobile.features.planning.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.uitopic.restockmobile.features.planning.presentation.screens.RecipeDetailScreen
import com.uitopic.restockmobile.features.planning.presentation.screens.RecipeFormScreen
import com.uitopic.restockmobile.features.planning.presentation.screens.RecipesListScreen
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel

// Navigation Routes
sealed class PlanningRoute(val route: String) {
    data object RecipesList : PlanningRoute("recipes_list")
    data object CreateRecipe : PlanningRoute("create_recipe")
    data object EditRecipe : PlanningRoute("edit_recipe/{recipeId}") {
        fun createRoute(recipeId: Int) = "edit_recipe/$recipeId"
    }
    data object RecipeDetail : PlanningRoute("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: Int) = "recipe_detail/$recipeId"
    }
}

fun NavGraphBuilder.planningNavGraph(
    navController: NavHostController,
    onUploadImage: suspend (Uri) -> String
) {
    navigation(
        startDestination = PlanningRoute.RecipesList.route,
        route = "planning"
    ) {
        // Recipes List
        composable(PlanningRoute.RecipesList.route) {
            RecipesListScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(PlanningRoute.RecipeDetail.createRoute(recipeId))
                },
                onCreateRecipe = {
                    navController.navigate(PlanningRoute.CreateRecipe.route)
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Create Recipe
        composable(PlanningRoute.CreateRecipe.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("planning")
            }
            val inventoryViewModel: InventoryViewModel = hiltViewModel(parentEntry)
            val customSupplies by inventoryViewModel.customSupplies.collectAsState()
            val supplies by inventoryViewModel.supplies.collectAsState()

            // Enrich customSupplies with full supply information
            val customSuppliesWithNames = customSupplies.map { custom ->
                val fullSupply = supplies.find { it.id == custom.supplyId } ?: custom.supply
                custom.copy(supply = fullSupply)
            }

            RecipeFormScreen(
                recipeId = null,
                customSupplies = customSuppliesWithNames,
                onNavigateBack = { navController.navigateUp() },
                onUploadImage = onUploadImage
            )
        }

        // Edit Recipe
        composable(
            route = PlanningRoute.EditRecipe.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: return@composable
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("planning")
            }
            val inventoryViewModel: InventoryViewModel = hiltViewModel(parentEntry)
            val customSupplies by inventoryViewModel.customSupplies.collectAsState()
            val supplies by inventoryViewModel.supplies.collectAsState()

            // Enrich customSupplies with full supply information
            val customSuppliesWithNames = customSupplies.map { custom ->
                val fullSupply = supplies.find { it.id == custom.supplyId } ?: custom.supply
                custom.copy(supply = fullSupply)
            }

            RecipeFormScreen(
                recipeId = recipeId,
                customSupplies = customSuppliesWithNames,
                onNavigateBack = { navController.navigateUp() },
                onUploadImage = onUploadImage
            )
        }

        // Recipe Detail
        composable(
            route = PlanningRoute.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: return@composable

            RecipeDetailScreen(
                recipeId = recipeId,
                onNavigateBack = { navController.navigateUp() },
                onEditRecipe = { id ->
                    navController.navigate(PlanningRoute.EditRecipe.createRoute(id))
                }
            )
        }
    }
}

// Extension function for NavController to navigate to Planning
fun NavHostController.navigateToPlanning() {
    navigate("planning") {
        launchSingleTop = true
    }
}