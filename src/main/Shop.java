package main;

import add.AddProduct;
import dao.DBConnection;
import shop.Category;
import shop.Product;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;

public class Shop extends JFrame implements ActionListener {

    private DBConnection dbCon;
    private Category category;
    private ArrayList<Category> categories;
    private ArrayList<Product> products;
    private JPanel mainPanel, categoriesPanel, productsPanel, addPanel, productsInfoPanel;
    private JList listProducts, listCategories;
    private JTextField newCategory;
    private JButton addCategory, addProduct;
    private DefaultListModel modelProducts, modelCategories;
    private AddProduct addProductMyClass;
    private JLabel productInfo;
    private boolean isCategorySelected, isProductAdded;
    private String listName;
    private int whichCategory;

    public static void main(String[] args) throws IOException {
        int czy;
        Scanner scanner = new Scanner(System.in);
        Shop shop = new Shop(500, 500);
        shop.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        shop.setVisible(true);
        do {
            System.out.println("Podaj nazwe kategorii: ");
            String categoryName = scanner.nextLine();
            shop.addCategoryToDataBase(categoryName);
            System.out.println("Czy chcesz dać kolejną?");
            czy = scanner.nextInt();
            scanner.nextLine(); // zjada \n którego nie zjada nextInt()
        } while (czy != 0);
        shop.loadCategories();
        ArrayList<Category> categories = shop.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            System.out.println("Dodaj produkty do kategorii: " + categories.get(i).getName());
            do {
                System.out.println("Podaj nazwę: ");
                String productName = scanner.nextLine();
                System.out.println("Podaj masę: ");
                float mass = scanner.nextFloat();
                System.out.println("Podaj cenę: ");
                float price = scanner.nextFloat();
                shop.addProducts(i + 1, productName, mass, price);
                System.out.println("Czy Dalej?");
                czy = scanner.nextInt();
                scanner.nextLine(); // zjada \n którego nie zjada nextInt()
            } while (czy == 1);
        }
        scanner.close();
    }

    public Shop() {
        super();
    }

    public Shop(int width, int height) {
        dbCon = DBConnection.getInstance();
        try {
            dbCon.createTableCategories();
            dbCon.createTableProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setMainPanel(width, height);
        setCategoriesPanel(width, height);
        setProductsPanel(width, height);
    }

    public void setMainPanel(int width, int height) {
        // panel całego okna
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        setTitle("Sklep");
        this.setSize(width, height);
        this.setVisible(true);
        this.setLayout(new BorderLayout());
    }

    public void setCategoriesPanel(int width, int height) {
        // panel kategorii
        categoriesPanel();
        // wczytanie kategorii
        loadCategories();
        // dodanie do panelu JList oraz wyświetlenie jej elementów przy użyciu DLM
        setCategoriesList(width, height);
        // panel do dodawania
        setAddCategory();
    }

    //===========================================

    public void categoriesPanel() {
        categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BorderLayout());
        mainPanel.add(categoriesPanel, BorderLayout.CENTER);
    }

    public void setCategoriesList(int width, int height) {
        listCategories = new JList();
        modelCategories = new DefaultListModel();
        for (int i = 0; i < categories.size(); i++) {
            modelCategories.addElement(categories.get(i).getName());
            listCategories.setModel(modelCategories);
        }
        listCategories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCategories.setLayoutOrientation(JList.VERTICAL);
        listCategories.setVisibleRowCount(-1);
        listCategories.setPreferredSize(new Dimension(width / 2, height));
        categoriesPanel.add(listCategories, BorderLayout.CENTER);
        JLabel panelLabel = new JLabel("KATEGORIE", SwingConstants.CENTER);
        // musi być żeby kolorował backgound
        panelLabel.setOpaque(true);
        panelLabel.setBackground(Color.WHITE);
        categoriesPanel.add(panelLabel, BorderLayout.NORTH);
        setScrollPane(listCategories, categoriesPanel);
    }

    public void setAddCategory() {
        addPanel = new JPanel();
        addPanel.setLayout(new GridLayout(1, 2));
        categoriesPanel.add(addPanel, BorderLayout.SOUTH);
        newCategory = new JTextField();
        newCategory.setColumns(15);
        newCategory.setToolTipText("Nowa kategoria");
        // actionListener żeby dodawało jak się kliknie enter
        newCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCategoryToDataBase();
            }
        });
        addPanel.add(newCategory);
        addCategory = new JButton("Dodaj");
        addCategory.addActionListener(this);
        addPanel.add(addCategory);
    }

    //=====================================================
    //=====================================================
    //=====================================================

    // metoda do całości panelu
    public void setProductsPanel(int width, int height) {
        // panel produktów
        productsPanel();
        // lista produktów
        setProductsList(width, height);
        // panel info o produktach
        setProductsInfoPanel(width, height);
        // ustawienie listenerów
        setAddProductListener();
        // listener do listy kategorii
        setListCategoriesListener();
        // listener do listy produktów
        setListProductsListener();
        // listener do prawego klikania na kategorię i usuwania
        setRightClickOnCategory();
        // listener do prawego klikania na produkt i usuwania
        setRightClickOnProduct();
    }

    // metoda ustawiająca panel produktów (cała prawa strona)
    public void productsPanel() {
        productsPanel = new JPanel();
        products = new ArrayList<>();
        productsPanel.setLayout(new BorderLayout());
        mainPanel.add(productsPanel, BorderLayout.EAST);
    }

    // metoda ustawiająca listę produktów
    public void setProductsList(int width, int height) {
        DefaultListModel model = new DefaultListModel();
        listProducts = new JList(model);
        listProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listProducts.setLayoutOrientation(JList.VERTICAL);
        listProducts.setVisibleRowCount(-1);
        listProducts.setPreferredSize(new Dimension(width / 2, height));
        productsPanel.add(listProducts, BorderLayout.CENTER);
        // opis panelu, SwingConstants.CENTER -> na środku
        JLabel panelLabel = new JLabel("PRODUKTY W KATEGORII", SwingConstants.CENTER);
        // musi być żeby kolorował background
        panelLabel.setOpaque(true);
        panelLabel.setBackground(Color.WHITE);
        productsPanel.add(panelLabel, BorderLayout.NORTH);
        setScrollPane(listProducts, productsPanel);
    }

    // metoda ustawiająca JPanel z info o produktach
    public void setProductsInfoPanel(int width, int height) {
        productsInfoPanel = new JPanel();
        productsInfoPanel.setPreferredSize(new Dimension(width / 2, height / 3));
        productsPanel.add(productsInfoPanel, BorderLayout.SOUTH);
        productInfo = new JLabel();
        productInfo.setPreferredSize(new Dimension(width / 2, height / 4));
        productsInfoPanel.add(productInfo, BorderLayout.CENTER);
        addProduct = new JButton("Dodaj produkt");
    }

    public void setAddProductListener() {
        addProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isCategorySelected) {
                    addProductMyClass = new AddProduct(whichCategory, listProducts, modelProducts, products, productInfo);
                    addProductMyClass.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    addProductMyClass.setVisible(true);
                    isProductAdded = true;
                } else {
                    productInfo.setText("Musisz wybrać kategorię!!!");
                }
            }
        });
    }

    public void setListCategoriesListener() {
        productsInfoPanel.add(addProduct, BorderLayout.SOUTH);
        isCategorySelected = false;
        // dodanie listenera do listy kategorii (musi być teraz, bo tu się aktualizuje lista produktów)
        listCategories.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                try {
                    productInfo.setText(null);
                    listProducts.clearSelection();
                } catch (NullPointerException exception) {

                }
                whichCategory = 0;
                if (!e.getValueIsAdjusting()) {
                    isCategorySelected = true;
                    try {
                        listName = listCategories.getSelectedValue().toString();
                        whichCategory = dbCon.categoryId(listName);
                        products = loadProductsFromCategory(whichCategory);
                        modelProducts = new DefaultListModel();
                        if(products.size() != 0) {
                            for (int i = 0; i < products.size(); i++) {
                                modelProducts.addElement(products.get(i).getName());
                                listProducts.setModel(modelProducts);
                            }
                        } else {
                               listProducts.setModel(modelProducts);
                        }
                    } catch (NullPointerException exception) {}
                }
            }
        });
    }

    public void setListProductsListener() {
        listProducts.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int whichProduct = 0;
                if (!e.getValueIsAdjusting()) {
                    try {
                        String productName = listProducts.getSelectedValue().toString();
                        while (productName != products.get(whichProduct).getName()) {
                            whichProduct++;
                        }
                        Product chosenProduct = products.get(whichProduct);
                        String name = chosenProduct.getName();
                        float mass = chosenProduct.getMass();
                        float price = chosenProduct.getPrice();
                        // <html></html> tym otaczam cały tekt <br/> tym łamię linię w JLabel
                        productInfo.setText("<html>Produkt: " + name + "<br/>Masa: " + mass + "<html><br/>Cena: " + price + "</html>");
                    } catch (NullPointerException exception){

                    }
                }
            }
        });
    }

    // usuwanie na prawym przycisku
    public void setRightClickOnCategory(){
        listCategories.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    Delete delete = new Delete("kategorię", listCategories.getSelectedValue().toString(), productInfo, modelProducts,
                                                    modelCategories, products, categories, listProducts, listCategories);
                    delete.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    delete.setVisible(true);
                }
            }
        });
    }

    // usuwanie na prawym przycisku produktu
    public void setRightClickOnProduct(){
        listProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    Delete delete = new Delete("produkt", listProducts.getSelectedValue().toString(), productInfo, modelProducts,
                                                    products, listProducts);
                    delete.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    delete.setVisible(true);
                }
            }
        });
    }

    public void setScrollPane(JList list, JPanel panel){
        JScrollPane listScroller = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(listScroller);
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    // metoda dodająca kateorie do bazy danych
    public void addCategoryToDataBase(String categoryName){
        try {
            dbCon.postCategory(categoryName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // metoda dodająca produkty do bazy danych
    public void addProducts(int foreign_key, String name, float mass, float price){
        dbCon = DBConnection.getInstance();
        try {
            dbCon.postProduct(foreign_key, name, mass, price);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // metoda wczytująca kategorie z bazy danych
    public void loadCategories(){
        categories = new ArrayList<>();
        String queryCategory = "SELECT * FROM `kategorie`";
        ResultSet rsCategory = dbCon.select(queryCategory);
        if(rsCategory != null){
            try {
                while(rsCategory.next()){
                    String name = rsCategory.getString("nazwa_kategorii");
                    category = new Category(name);
                    categories.add(category);
                }
                dbCon.destroy(rsCategory);
            } catch (SQLException e) {
                System.out.println("Kategorie: Problem z przetworzeniem danych.");
                System.out.println("SQLException: " + e.getMessage());
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("VendorError: " + e.getErrorCode());
                e.printStackTrace();
            }
        }
        dbCon.destroy(rsCategory);
    }

    // metoda wczytująca z bazy danych
    public ArrayList<Product> loadProductsFromCategory(int foreign_key){
        dbCon = DBConnection.getInstance();
        products = new ArrayList<>();
        String queryProducts = "SELECT * FROM `produkty` WHERE `foreign_key` = " + foreign_key;
        ResultSet rsProducts = dbCon.select(queryProducts);
        if(rsProducts != null){
            try {
                while (rsProducts.next()) {
                    String name = rsProducts.getString("nazwa_produktu");
                    float mass = rsProducts.getFloat("masa");
                    float price = rsProducts.getFloat("cena");
                    Product product = new Product(name, mass, price);
                    products.add(product);
                }
                dbCon.destroy(rsProducts);
            } catch (SQLException e) {
                System.out.println("Produkty: Problem z przetworzeniem danych.");
                System.out.println("SQLException: " + e.getMessage());
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("VendorError: " + e.getErrorCode());
                e.printStackTrace();
            }
        }
        dbCon.destroy(rsProducts);
        return products;
    }

    public void addCategoryToDataBase(){
        String name = newCategory.getText();
        newCategory.setText("");
        if(name.length() > 0) {
            productInfo.setText("");
            // utworzenie nowej kategorii i dodanie jej do ArrayList categories
            Category category = new Category(name);
            categories.add(category);
            // dodanie kategorii do bazy danych
            addCategoryToDataBase(name);
            // dodanie kategorii do JList listCategories wyświetlanej na ekranie
            modelCategories.addElement(name);
            listCategories.setModel(modelCategories);
            // usunięcie JList listProducts z ekranu (jeśli istnieje)
            if(listProducts != null) {
                modelProducts = new DefaultListModel();
                listProducts.setModel(modelProducts);
            }
        } else {
            productInfo.setText("Musisz podać nazwę kategorii!");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand() == "Dodaj"){
            addCategoryToDataBase();
        }
    }
}