package ggc.products;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Recipe implements Serializable {
    
    private boolean _alreadyBuilt = false;

    private LinkedHashMap<String, Integer> _products;
    private LinkedList<RecipeComponent> _recipe;

    private String _preBuiltRecipe;

    public Recipe(LinkedHashMap<String, Integer> products){
        _products = products;
        buildRecipe();
    }

    public Recipe(String recipe){
        _alreadyBuilt = true;
        setRecipe(recipe);
        buildMap(recipe);
    }

    private void buildMap(String recipe){
        _recipe = new LinkedList<>();
        String fields[] = recipe.split("#");

        for(String s : fields){
            String comps[] = s.split(":");
            RecipeComponent nc = new RecipeComponent(comps[0], Integer.parseInt(comps[1]));
            _recipe.add(nc);
        }
    }

    public List<String> ingredients(){
        List<String> ingr = new ArrayList<>();
        
        for(RecipeComponent rc : _recipe){
            ingr.add(rc.getProduct());
        }

        return ingr;
    }

    public LinkedList<RecipeComponent> components(){
        return _recipe;
    }

    private void setRecipe(String recipe) {
        this._preBuiltRecipe = recipe;
    }

    public void buildRecipe(){
        _recipe = new LinkedList<>();
        for(String comp : _products.keySet()){
            RecipeComponent rc = new RecipeComponent(comp, _products.get(comp));
            _recipe.add(rc);
        }
    }

    public String toString(){
        if(_alreadyBuilt)
            return _preBuiltRecipe;

        StringBuilder result = new StringBuilder();
        for(RecipeComponent rc : _recipe){
            result.append(rc.getProduct() + ":" + rc.getQuantity() + "#");
        }
        result.deleteCharAt(result.length()-1);
        return result.toString();
    }

}
