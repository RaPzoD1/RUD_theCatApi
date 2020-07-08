import com.google.gson.Gson;
import okhttp3.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GatosService {



    public static void verGatitos() throws IOException {
        //1. Vamos a trael los datos de la api

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/images/search")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        String elJson = response.body().string();

        //cortar los corchetes

        elJson =  elJson.substring(1,elJson.length());
        elJson = elJson.substring(0,elJson.length()-1);

        //crear un objeto d ela clase Gson

        Gson gson = new Gson();

        Gatos gatos = gson.fromJson(elJson,Gatos.class);

        //redimensionar en caso necesitar

        Image image = null;
        try {
            URL url = new URL(gatos.getUrl());
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.addRequestProperty("User-Agent", "");
            BufferedImage bufferedImage = ImageIO.read(httpcon.getInputStream());

            ImageIcon fondogato = new ImageIcon(bufferedImage);

            if (fondogato.getIconWidth() > 800){
                //redimensionamos
                Image fondo = fondogato.getImage();
                Image modificada = fondo.getScaledInstance(800,600, Image.SCALE_SMOOTH);
                fondogato = new ImageIcon(modificada);
            }

            String menu = "Opciones: \n1.Ver otra imagen \n2. Favoritos \n3.Volver";

            String[] botones = {"Ver otra imagen","Favoritos","Volver"};
            String id_gato = gatos.getId();
            String opcion = (String) JOptionPane.showInputDialog(
                    null,
                    menu,
                    id_gato,
                    JOptionPane.INFORMATION_MESSAGE,
                    fondogato,
                    botones,
                    botones[0]
            );

            int seleccion = -1;
            for (int i = 0; i < botones.length; i++) {
                if(opcion.equals(botones[i])){
                    seleccion = i;
                }
            }

            switch (seleccion){
                case 0:
                    verGatitos();
                    break;
                case 1:
                    favoritoGato(gatos);
                    break;
                default:
                    break;

            }


        }catch (IOException e){
            System.out.println(e);
        }
    }
    public static void favoritoGato(Gatos gato){

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\r\n  \"image_id\": \""+gato.getId()+"\"  \r\n}");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", gato.getApiKey())
                    .build();
            Response response = client.newCall(request).execute();

        }catch (IOException e){
            System.out.println(e);
        }

    }
    public static void verFavoritos(String apiKey) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/favourites")
                .method("GET", null)
                .addHeader("x-api-key", apiKey)
                .build();
        Response response = client.newCall(request).execute();
        //guardamos la respuesta
        String elJson = response.body().string();
        //creamos el objeto GSon
        Gson gson = new Gson();
        GatosFavoritos[] gatosArray = gson.fromJson(elJson,GatosFavoritos[].class);

        if(gatosArray.length > 0){
            int min = 1;
            int max = gatosArray.length;
            int aleatorio = (int) (Math.random() * ((max-min)-1)) + min;
            int indice = aleatorio - 1;

            GatosFavoritos gatosFavoritos = gatosArray[indice];

            Image image = null;
            try {
                URL url = new URL(gatosFavoritos.image.getUrl());
                HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                httpcon.addRequestProperty("User-Agent", "");
                BufferedImage bufferedImage = ImageIO.read(httpcon.getInputStream());

                ImageIcon fondogato = new ImageIcon(bufferedImage);

                if (fondogato.getIconWidth() > 800){
                    //redimensionamos
                    Image fondo = fondogato.getImage();
                    Image modificada = fondo.getScaledInstance(800,600, Image.SCALE_SMOOTH);
                    fondogato = new ImageIcon(modificada);
                }

                String menu = "Opciones: \n1.Ver otra imagen \n2. Eliminar favorito \n3.Volver";

                String[] botones = {"Ver otra imagen","Eliminar favorito","Volver"};
                String id_gato = gatosFavoritos.getId();
                String opcion = (String) JOptionPane.showInputDialog(
                        null,
                        menu,
                        id_gato,
                        JOptionPane.INFORMATION_MESSAGE,
                        fondogato,
                        botones,
                        botones[0]
                );

                int seleccion = -1;
                for (int i = 0; i < botones.length; i++) {
                    if(opcion.equals(botones[i])){
                        seleccion = i;
                    }
                }

                switch (seleccion){
                    case 0:
                        verFavoritos(apiKey);
                        break;
                    case 1:
                        borrarFavorito(gatosFavoritos);
                        break;
                    default:
                        break;

                }


            }catch (IOException e){
                System.out.println(e);
            }
        }
    }
    public static void borrarFavorito(GatosFavoritos gatosFavoritos){
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites/"+gatosFavoritos.getId()+"")
                    .method("DELETE", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", gatosFavoritos.getApikey())
                    .build();
            Response response = client.newCall(request).execute();
            if ( response.code() == 200){
                JOptionPane.showMessageDialog(null,"Gato favorito"+ gatosFavoritos.getId()+" fue eliminado");
            }
            else {
                JOptionPane.showMessageDialog(null,"Algo fallo no se pudo eliminar");
            }
        }catch (IOException e){
            System.out.println(e);
        }
    }
}
