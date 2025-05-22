package lanchonete;
import dao.Conexao;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import views.TelaCadastroProduto;
import views.TelaControleEstoque;
import views.TelaGestaoClientes;
import views.TelaGerenciamentoPedidos;
import views.TelaRelatorios;

public class Lanchonete extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button btnCadastroProduto = new Button("Cadastro de Produto");
        btnCadastroProduto.setOnAction(e -> {
            TelaCadastroProduto tela = new TelaCadastroProduto();
            tela.setVisible(true);
        });

        Button btnControleEstoque = new Button("Controle de Estoque");
        btnControleEstoque.setOnAction(e -> {
            TelaControleEstoque tela = new TelaControleEstoque();
            tela.setVisible(true);
        });

        Button btnGestaoClientes = new Button("Gestão de Clientes");
        btnGestaoClientes.setOnAction(e -> {
            TelaGestaoClientes tela = new TelaGestaoClientes();
            tela.setVisible(true);
        });

        Button btnGerenciamentoPedidos = new Button("Gerenciamento de Pedidos");
        btnGerenciamentoPedidos.setOnAction(e -> {
            TelaGerenciamentoPedidos tela = new TelaGerenciamentoPedidos();
            tela.setVisible(true);
        });

        Button btnRelatorios = new Button("Relatórios");
        btnRelatorios.setOnAction(e -> {
            TelaRelatorios tela = new TelaRelatorios();
            tela.setVisible(true);
        });

        VBox root = new VBox(10, btnCadastroProduto, btnControleEstoque, btnGestaoClientes, btnGerenciamentoPedidos, btnRelatorios);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root, 350, 300);

        primaryStage.setTitle("Menu Principal - Lanchonete");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}