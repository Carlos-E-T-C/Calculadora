package br.feevale.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;



import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Inicializa o ListView
        listView = findViewById(R.id.listView);

        // Cria um ArrayAdapter com a lista de histórico como fonte de dados
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MainActivity.listaHistorico);

        // Define o adapter no ListView
        listView.setAdapter(adapter);
    }

    public void cliqueVoltar(View v) {
        finish(); // Fecha a atividade de histórico e retorna à atividade anterior
    }
}
