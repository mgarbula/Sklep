package shop;

import java.util.ArrayList;

public class Category {

    String name;
    ArrayList<Product> productsInCategory;

    public Category(String name){
        this.name = name;
    }

    public Category(String name, ArrayList<Product> productsInCategory){
        this.name = name;
        this.productsInCategory = productsInCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Product> getProductsInCategory() {
        return productsInCategory;
    }

    public void setProductsInCategory(ArrayList<Product> productsInCategory) {
        this.productsInCategory = productsInCategory;
    }
}
