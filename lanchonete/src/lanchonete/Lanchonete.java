package lanchonete;

import connection.Conexao;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import views.TelaCadastroProduto;
import views.TelaControleEstoque;
import views.TelaGestaoClientes;
import views.TelaGerenciamentoPedidos;
import views.TelaRelatorios;

public class Lanchonete {
    private JFrame frame;

    public Lanchonete() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        frame = new JFrame("Menu Principal - Lanchonete");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnCadastroProduto = new JButton("Cadastro de Produto");
        btnCadastroProduto.addActionListener(e -> {
            TelaCadastroProduto tela = new TelaCadastroProduto();
            tela.setVisible(true);
        });

        JButton btnControleEstoque = new JButton("Controle de Estoque");
        btnControleEstoque.addActionListener(e -> {
            TelaControleEstoque tela = new TelaControleEstoque();
            tela.setVisible(true);
        });

        JButton btnGestaoClientes = new JButton("Gestão de Clientes");
        btnGestaoClientes.addActionListener(e -> {
            TelaGestaoClientes tela = new TelaGestaoClientes();
            tela.setVisible(true);
        });

        JButton btnGerenciamentoPedidos = new JButton("Gerenciamento de Pedidos");
        btnGerenciamentoPedidos.addActionListener(e -> {
            TelaGerenciamentoPedidos tela = new TelaGerenciamentoPedidos();
            tela.setVisible(true);
        });

        JButton btnRelatorios = new JButton("Relatórios");
        btnRelatorios.addActionListener(e -> {
            TelaRelatorios tela = new TelaRelatorios();
            tela.setVisible(true);
        });

        panel.add(btnCadastroProduto);
        panel.add(btnControleEstoque);
        panel.add(btnGestaoClientes);
        panel.add(btnGerenciamentoPedidos);
        panel.add(btnRelatorios);

        frame.add(panel);
        frame.setSize(350, 300);
        frame.setLocationRelativeTo(null);
    }

    public void mostrar() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        new Lanchonete().mostrar();
    }
}