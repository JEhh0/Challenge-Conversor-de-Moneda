package conversor;

import java.awt.EventQueue;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ConverMoneda {

    private JFrame frame;
    private JButton boton;
    private JComboBox<Moneda> listspl;
    private JTextField text;
    private JLabel label;

    public enum Moneda {
        cop_dolar,
        cop_euro,
        cop_libra,
        dolar_cop,
        euro_cop,
        libra_cop
    }

    public double valorInput = 0;
    public double dolar;
    public double euro;
    public double libra;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ConverMoneda window = new ConverMoneda();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ConverMoneda() {
        initialize();
        obtenerTasasDeCambio();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        text = new JTextField();
        text.setBounds(10, 81, 96, 19);
        frame.getContentPane().add(text);
        text.setColumns(10);

        listspl = new JComboBox<>();
        listspl.setModel(new DefaultComboBoxModel<>(Moneda.values()));
        listspl.setBounds(10, 173, 96, 21);
        frame.getContentPane().add(listspl);

        boton = new JButton("Convertir");
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Convertir();
            }
        });
        boton.setForeground(new Color(0, 128, 255));
        boton.setBounds(127, 173, 130, 19);
        frame.getContentPane().add(boton);

        label = new JLabel("00.00");
        label.setBounds(127, 81, 299, 19);
        frame.getContentPane().add(label);
    }

    public void Convertir() {
        if (Validar(text.getText())) {
            Moneda moneda = (Moneda) listspl.getSelectedItem();

            double tasaConversion = 0;
            switch (moneda) {
                case cop_dolar:
                    tasaConversion = 1 / dolar;
                    break;
                case cop_euro:
                    tasaConversion = 1 / euro;
                    break;
                case cop_libra:
                    tasaConversion = 1 / libra;
                    break;
                case dolar_cop:
                    tasaConversion = dolar;
                    break;
                case euro_cop:
                    tasaConversion = euro;
                    break;
                case libra_cop:
                    tasaConversion = libra;
                    break;
            }

            double resultado = valorInput * tasaConversion;
            label.setText(Redondear(resultado));
        }
    }

    public String Redondear(double valor) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(valor);
    }

    public boolean Validar(String texto) {
        try {
            double x = Double.parseDouble(texto);
            if (x > 0) {
                valorInput = x;
                return true;
            }
        } catch (NumberFormatException e) {
            label.setText("Solamente se permite numeros como input");
        }
        return false;
    }

    private void obtenerTasasDeCambio() {
        try {
            // URL para obtener las tasas de cambio actualizadas
            String url_str = "https://v6.exchangerate-api.com/v6/dd305eef47d655d531361be6/latest/USD";

            // Realizando la solicitud a la API
            URL url = new URL(url_str);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convertir la respuesta a JSON
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonobj = root.getAsJsonObject();

            // Obtener las tasas de cambio
            JsonObject rates = jsonobj.getAsJsonObject("conversion_rates");
            dolar = rates.get("COP").getAsDouble(); // Tasa de conversión de USD a COP
            euro = rates.get("EUR").getAsDouble(); // Tasa de conversión de EUR a COP
            libra = rates.get("GBP").getAsDouble(); // Tasa de conversión de GBP a COP

        } catch (Exception e) {
            e.printStackTrace();
            label.setText("Error al obtener tasas de cambio");
        }
    }
}


