package tasks.foodTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskService {
    public int calculateAllKcal() {
        var result = 0;
        List<Recipe> recipeList = createExample();
        for (Recipe recipe : recipeList) {
            for (Component component : recipe.componentList()) {
                result = result + component.kcal();
            }
        }
        return result;
    }

    public int calculateAverageKcalRecipes() {
        var kcal = 0;
        List<Recipe> recipeList = createExample();
        for (Recipe recipe : recipeList) {
            for (Component component : recipe.componentList()) {
                kcal = kcal + component.kcal();
            }
        }
        return kcal / recipeList.size();
    }

    public int calculateTheMostKcalRecipe() {
        var kcal = 0;
        var comparingKcal = 0;
        List<Recipe> recipeList = createExample();
        for (Recipe recipe : recipeList) {
            for (Component component : recipe.componentList()) {
                kcal = kcal + component.kcal();
            }
            if (kcal >= comparingKcal) {
                comparingKcal = kcal;
            }
            kcal = 0;
        }
        return comparingKcal;
    }

    public String getTheMostKcalRecipeName() {
        var kcal = 0;
        var comparingKcal = 0;
        var result = "";
        List<Recipe> recipeList = createExample();
        for (Recipe recipe : recipeList) {
            for (Component component : recipe.componentList()) {
                kcal = kcal + component.kcal();
            }
            if (kcal >= comparingKcal) {
                comparingKcal = kcal;
                result = recipe.name();
            }
            kcal = 0;
        }
        return result;
    }


    private List<Recipe> createExample() {
        var component1 = createComponent("banana", 100);
        var component2 = createComponent("apple", 50);
        var component3 = createComponent("egg", 30);
        var component4 = createComponent("milk", 40);

        List<Component> componentList1 = createComponentList(component1, component4, component3);
        List<Component> componentList2 = createComponentList(component2, component4, component3);
        List<Component> componentList3 = createComponentList(component1, component2, component4);
        var recipe1 = createRecipe("banana pie", componentList1);
        var recipe2 = createRecipe("apple pie", componentList2);
        var recipe3 = createRecipe("smoothie", componentList3);
        return createRecipeList(recipe1, recipe2, recipe3);
    }

    private List<Recipe> createRecipeList(Recipe... recipes) {
        return new ArrayList<>(Arrays.asList(recipes));
    }

    private Recipe createRecipe(String name, List<Component> componentList) {
        return new Recipe(name, componentList);
    }

    private List<Component> createComponentList(Component... components) {
        return new ArrayList<>(Arrays.asList(components));
    }

    private Component createComponent(String name, int kcal) {
        return new Component(name, kcal);
    }
}
