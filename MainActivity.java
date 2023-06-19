package br.feevale.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Lista de histórico
    public static List<String> listaHistorico;
    private EditText campoExpressao;
    private TextView campoResultado;
    private boolean insercaoHabilitada;

    private EditText etExpressao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etExpressao = findViewById(R.id.etExpressao);
        etExpressao.setKeyListener(null);

        // Inicializa a lista de histórico
        listaHistorico = new ArrayList<>();

        // Encontra as visualizações EditText e TextView pelo ID definido no layout XML
        campoExpressao = findViewById(R.id.etExpressao);
        campoResultado = findViewById(R.id.tvResultado);

        // Configura o ouvinte de teclado do campo de expressão
        campoExpressao.setOnKeyListener((view, keyCode, event) -> {
            // Retorna verdadeiro para impedir que o teclado físico ou virtual atualize o campo de expressão
            return true;
        });

        // Desabilita o teclado virtual para o campo de expressão
        campoExpressao.setShowSoftInputOnFocus(false);

        // Configura o ouvinte de clique do botão "Limpar"
        Button botaoLimpar = findViewById(R.id.btnLimpar);
        botaoLimpar.setOnClickListener(v -> {
            // Limpa os campos de entrada de expressão e resultado
            campoExpressao.setText("");
            campoResultado.setText("");
            campoExpressao.requestFocus(); // Define o foco para o campo campoExpressao
            habilitarInsercao();
        });

        // Configura o ouvinte de clique do botão "Histórico"
        Button botaoHistorico = findViewById(R.id.btnHistorico);
        botaoHistorico.setOnClickListener(v -> {
            String expressao = campoExpressao.getText().toString().trim();
            String resultado = campoResultado.getText().toString().trim();

            // Cria uma Intent para abrir a atividade de histórico
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putExtra("expressao", expressao);
            intent.putExtra("resultado", resultado);
            startActivity(intent);
        });

        // Configura os ouvintes de clique dos botões numéricos e operadores
        Button botaoZero = findViewById(R.id.btnZero);
        botaoZero.setOnClickListener(v -> adicionarTexto("0"));

        Button botaoUm = findViewById(R.id.btnUm);
        botaoUm.setOnClickListener(v -> adicionarTexto("1"));

        Button botaoDois = findViewById(R.id.btnDois);
        botaoDois.setOnClickListener(v -> adicionarTexto("2"));

        Button botaoTres = findViewById(R.id.btnTres);
        botaoTres.setOnClickListener(v -> adicionarTexto("3"));

        Button botaoQuatro = findViewById(R.id.btnQuatro);
        botaoQuatro.setOnClickListener(v -> adicionarTexto("4"));

        Button botaoCinco = findViewById(R.id.btnCinco);
        botaoCinco.setOnClickListener(v -> adicionarTexto("5"));

        Button botaoSeis = findViewById(R.id.btnSeis);
        botaoSeis.setOnClickListener(v -> adicionarTexto("6"));

        Button botaoSete = findViewById(R.id.btnSete);
        botaoSete.setOnClickListener(v -> adicionarTexto("7"));

        Button botaoOito = findViewById(R.id.btnOito);
        botaoOito.setOnClickListener(v -> adicionarTexto("8"));

        Button botaoNove = findViewById(R.id.btnNove);
        botaoNove.setOnClickListener(v -> adicionarTexto("9"));

        Button botaoPonto = findViewById(R.id.btnPonto);
        botaoPonto.setOnClickListener(v -> adicionarTexto("."));

        Button botaoMais = findViewById(R.id.btnMais);
        botaoMais.setOnClickListener(v -> adicionarTexto("+"));

        Button botaoMenos = findViewById(R.id.btnMenos);
        botaoMenos.setOnClickListener(v -> adicionarTexto("-"));

        Button botaoMultiplicar = findViewById(R.id.btnMultiplicar);
        botaoMultiplicar.setOnClickListener(v -> adicionarTexto("*"));

        Button botaoDividir = findViewById(R.id.btnDividir);
        botaoDividir.setOnClickListener(v -> adicionarTexto("/"));

        Button botaoIgual = findViewById(R.id.btnIgual);
        botaoIgual.setOnClickListener(this::calcular);

        // Habilita a inserção no campo de expressão
        habilitarInsercao();

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/eval.html");
    }

    // Método chamado ao clicar no botão de calcular
    public void calcular(View v) {
        if (!insercaoHabilitada) {
            return; // Sai do método se a inserção não estiver habilitada
        }

        String expressao = campoExpressao.getText().toString().trim();

        try {
            double resultado = avaliarExpressao(expressao);
            exibirResultado(resultado);

            adicionarAoHistorico(expressao, resultado);

            // Desabilita a inserção no campo de expressão após o cálculo
            desabilitarInsercao();
        } catch (NumberFormatException e) {
            exibirErro("Valores numéricos inválidos.");
        } catch (IllegalArgumentException e) {
            exibirErro(e.getMessage());
        } catch (Exception e) {
            exibirErro("Expressão inválida.");
        }
    }

    // Exibe o resultado no campo de resultado
    private void exibirResultado(double resultado) {
        campoResultado.setText("= " + resultado);
    }

    // Exibe uma mensagem de erro no campo de resultado
    private void exibirErro(String mensagemErro) {
        campoResultado.setText("Erro: " + mensagemErro);
    }

    // Adiciona a expressão e o resultado ao histórico
    private void adicionarAoHistorico(String expressao, double resultado) {
        if (resultado != 0.0) {
            String itemHistorico = expressao + " = " + String.valueOf(resultado);
            listaHistorico.add(itemHistorico);
        }
    }

    // Avalia a expressão usando um WebView com JavaScript
    private double avaliarExpressao(String expressao) {
        WebView webView = findViewById(R.id.webView);
        webView.evaluateJavascript("evalExpression('" + expressao + "')", value -> {
            if (value != null && !value.isEmpty() && !"null".equals(value)) {
                double resultado = Double.parseDouble(value);
                exibirResultado(resultado);

                adicionarAoHistorico(expressao, resultado);

                // Desabilita a inserção no campo de expressão após o cálculo
                desabilitarInsercao();
            } else {
                exibirErro("Expressão inválida.");
            }
        });

        // Retorna um valor temporário, pois o resultado é obtido de forma assíncrona
        return 0.0;
    }

    // Converte uma string em um número double
    private double converterParaNumero(String token) {
        NumberFormat format = NumberFormat.getInstance();
        try {
            return format.parse(token).doubleValue();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Valores numéricos inválidos.");
        }
    }

    // Calcula o resultado da expressão usando dois números e um operador
    private double calcular(double numero1, char operador, double numero2) {
        switch (operador) {
            case '+':
                return numero1 + numero2;
            case '-':
                return numero1 - numero2;
            case '*':
                return numero1 * numero2;
            case '/':
                if (numero2 == 0) {
                    throw new IllegalArgumentException("Divisão por zero.");
                }
                return numero1 / numero2;
            default:
                throw new IllegalArgumentException("Operador inválido: " + operador);
        }
    }

    // Verifica se uma string é um operador válido
    private boolean ehOperador(String token) {
        return token.length() == 1 && "+-*/".contains(token);
    }

    // Adiciona um texto ao campo de expressão
    private void adicionarTexto(String texto) {
        int start = campoExpressao.getSelectionStart();
        int end = campoExpressao.getSelectionEnd();

        if (start != end) {
            campoExpressao.getText().replace(Math.min(start, end), Math.max(start, end), texto, 0, texto.length());
        } else {
            campoExpressao.getText().insert(Math.min(start, end), texto);
        }
    }

    // Habilita a inserção no campo de expressão
    private void habilitarInsercao() {
        insercaoHabilitada = true;
        campoExpressao.setCursorVisible(true);
        campoExpressao.setFocusableInTouchMode(true);
        campoExpressao.setFocusable(true);
        campoExpressao.setSelection(campoExpressao.getText().length());
    }

    // Desabilita a inserção no campo de expressão
    private void desabilitarInsercao() {
        insercaoHabilitada = false;
        campoExpressao.setCursorVisible(false);
        campoExpressao.setFocusableInTouchMode(false);
        campoExpressao.setFocusable(false);
    }
}
