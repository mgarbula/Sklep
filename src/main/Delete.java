package main;

import dao.DBConnection;
import shop.Category;
import shop.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Delete extends JFrame  {

    private DBConnection dbCon;
    private JPanel mainPanel, buttonsPanel;
    private JButton yes, no;
    private JLabel info;
    private DefaultListModel modelProducts, modelCategories;
    private ArrayList<Product> products;
    private ArrayList<Category> categories;
    private JList listProducts, listCategories;
    String what, name;

    public Delete(String what, String name, JLabel info, DefaultListModel modelProducts, ArrayList<Product> products,
                        JList listProducts){
        this.what = what;
        this.name = name;
        this.info = info;
        this.modelProducts = modelProducts;
        this.products = products;
        this.listProducts = listProducts;
        setMainPanel();
        setLabel(what);
        setButtonsPanel();
        setYesButton();
        setNoButton();
    }

    public Delete(String what, String name, JLabel info, DefaultListModel modelProducts, DefaultListModel modelCategories,
                    ArrayList<Product> products, ArrayList<Category> categories, JList listProducts, JList listCategories){
        this.what = what;
        this.name = name;
        this.info = info;
        this.modelProducts = modelProducts;
        this.modelCategories = modelCategories;
        this.products = products;
        this.categories = categories;
        this.listProducts = listProducts;
        this.listCategories = listCategories;
        setMainPanel();
        setLabel(what);
        setButtonsPanel();
        setYesButton();
        setNoButton();
    }

    public void setMainPanel(){
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        this.setSize(300, 100);
        this.setVisible(true);
        this.setLayout(new BorderLayout());
    }

    public void setLabel(String what){
        JLabel label = new JLabel("Czy na pewno chcesz usunąć " + what + " " + name + "?", SwingConstants.CENTER);
        mainPanel.add(label, BorderLayout.CENTER);
    }

    public void setButtonsPanel(){
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2));
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    public void setYesButton(){
        yes = new JButton("TAK");
        buttonsPanel.add(yes);
        yes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(what.equals("kategorię")){
                    setCategories();
                    deleteCategory();
                    deleteAllProducts();
                } else if(what.equals("produkt")){
                    setProducts();
                    deleteProduct();
                    info.setText("Usunięto produkt " + name + ".");
                }
                dispose();
            }
        });
    }

    public void setNoButton(){
        no = new JButton("NIE");
        buttonsPanel.add(no);
        no.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    // usunięcie produktu z bazy danych
    public void deleteProduct(){
        dbCon = DBConnection.getInstance();
        try {
            dbCon.deleteProduct(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // usunięcie produktu z listy i odświeżenie widoku
    public void setProducts(){
        // usunięcie produktu z ArrayList produktów
        for(int i = 0; i < products.size(); i++){
            if(products.get(i).getName().equals(name)){
                listProducts.clearSelection();
                modelProducts.remove(i);
                listProducts.setModel(modelProducts);
                products.remove(i);
                break;
            }
        }
    }

    // usunięcie kategorii z bazy danych wraz ze wszystkim produktami z tej kategorii (w metodzie deleteCategory())
    public void deleteCategory(){
        dbCon = DBConnection.getInstance();
        dbCon.deleteCategory(name);
    }

    public void setCategories(){
        for(int i = 0; i < categories.size(); i++){
            if(categories.get(i).getName().equals(name)){
                listCategories.clearSelection();
                modelCategories.remove(i);
                listCategories.setModel(modelCategories);
                categories.remove(i);
                break;
            }
        }
    }

    public void deleteAllProducts(){
        int size = products.size();
        for(int i = 0; i < size; i++){
            listProducts.clearSelection();
            modelProducts.remove(0);
            products.remove(0);
        }
        listProducts.setModel(modelProducts);
    }
}
