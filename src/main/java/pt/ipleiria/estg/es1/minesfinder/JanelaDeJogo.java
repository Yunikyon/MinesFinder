package pt.ipleiria.estg.es1.minesfinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JanelaDeJogo extends JFrame{
    private JPanel painelJogo; // painel do jogo. O nome é definido no modo Design, em "field name"
    private botaoCampoMinado[][] botoes;
    private CampoMinado campoMinado;

    private TabelaRecordes recordes;

    public JanelaDeJogo(String title, CampoMinado campoMinado, TabelaRecordes tabela) {
        super(title);

        int largura = campoMinado.getLargura();
        int altura = campoMinado.getAltura();
        this.campoMinado = campoMinado;
        this.recordes = tabela;

        this.botoes = new botaoCampoMinado[largura][altura];

        painelJogo.setLayout(new GridLayout(altura,largura));

        // Adicionar os botões
        for (int coluna=0; coluna<altura; coluna++) {
            for (int linha=0; linha<largura; linha++){
                //btn.setText("C"+coluna+"L"+linha);
                // btn.setEstado(linha);
                botoes[linha][coluna] = new botaoCampoMinado(linha, coluna);
                botoes[linha][coluna].addActionListener(this::btnCampoMinadoActionPerformed);
                botoes[linha][coluna].addMouseListener(mouseListener);
                botoes[linha][coluna].addKeyListener(keyListener);
                painelJogo.add(botoes[linha][coluna]);
            }
        }

        setContentPane(painelJogo);
        // Destrói esta janela, removendo-a completamente da memória.
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    public void btnCampoMinadoActionPerformed(ActionEvent e) {
        var botao = (botaoCampoMinado) e.getSource();
        int x = botao.getLinha();
        int y = botao.getColuna();
        campoMinado.revelarQuadricula(x, y);
        actualizarEstadoBotoes();

        if (campoMinado.isJogoTerminado()) {
            if (campoMinado.isJogadorDerrotado()) {
                JOptionPane.showMessageDialog(null, "Oh, rebentou uma mina", "Perdeu!", JOptionPane.INFORMATION_MESSAGE);
            }else {
                JOptionPane.showMessageDialog(null, "Parabéns. Conseguiu descobrir todas as minas em " +
                        (campoMinado.getDuracaoJogo() / 1000) + " segundos", "Vitória", JOptionPane.INFORMATION_MESSAGE);

                boolean novoRecorde = campoMinado.getDuracaoJogo()<recordes.getTempoJogo();
                if (novoRecorde) {
                    String nome = JOptionPane.showInputDialog("Introduza o seu nome");
                    recordes.setRecorde(nome, campoMinado.getDuracaoJogo());
                }
            }

            setVisible(false);


        }
    }

    private void actualizarEstadoBotoes() {
        for (int x = 0; x < campoMinado.getLargura(); x++) {
            for (int y = 0; y < campoMinado.getAltura(); y++) {
                botoes[x][y].setEstado(campoMinado.getEstadoQuadricula(x, y));
            }
        }

        if(campoMinado.isVitoria()){
            campoMinado.jogoTerminado = true;
            return;
        }
    }

    MouseListener mouseListener=new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
        }
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() != MouseEvent.BUTTON3) {
                return;
            }
            var botao = (botaoCampoMinado) e.getSource();
            var x = botao.getLinha();
            var y = botao.getColuna();
            var estadoQuadricula = campoMinado.getEstadoQuadricula(x, y);
            switchEstado(botao.getLinha(), botao.getColuna());
            actualizarEstadoBotoes();
        }
        @Override
        public void mouseReleased(MouseEvent e) {
        }
        @Override
        public void mouseEntered(MouseEvent e) {
        }
        @Override
        public void mouseExited(MouseEvent e) {
        }
    };

    KeyListener keyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }
        @Override
        public void keyPressed(KeyEvent e) {
            var botao = (botaoCampoMinado) e.getSource();
            var linha = botao.getLinha();
            var coluna = botao.getColuna();
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> botoes[linha][--coluna < 0 ? campoMinado.getAltura() - 1 :
                        coluna].requestFocus();
                case KeyEvent.VK_DOWN -> botoes[linha][(coluna + 1) %
                        campoMinado.getAltura()].requestFocus();
                case KeyEvent.VK_LEFT -> botoes[--linha < 0 ? campoMinado.getLargura() - 1 :
                        linha][coluna].requestFocus();
                case KeyEvent.VK_RIGHT -> botoes[(linha + 1) %
                        campoMinado.getLargura()][coluna].requestFocus();
                case KeyEvent.VK_M -> {
                    switchEstado(linha, coluna);
                    actualizarEstadoBotoes();
                }
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
        }
    };

    private void switchEstado(int linha, int coluna) {
        switch (campoMinado.getEstadoQuadricula(linha, coluna)) {
            case CampoMinado.TAPADO ->
                    campoMinado.marcarComoTendoMina(linha, coluna);
            case CampoMinado.MARCADO ->
                    campoMinado.marcarComoSuspeita(linha, coluna);
            case CampoMinado.DUVIDA ->
                    campoMinado.desmarcarQuadricula(linha, coluna);
        }
    }


}
