package projeto.persistencia;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import projeto.investimentos.Açao;
import projeto.investimentos.Criptomoeda;
import projeto.investimentos.Fii;
import projeto.investimentos.FinancialAsset;

/**
 * Fábrica central do {@link Gson} usado na persistência do sistema.
 *
 * <p>Registra um desserializador que reconstrói a subclasse correta de
 * {@link FinancialAsset} a partir do campo {@code tipo}, evitando duplicar essa
 * configuração entre a carteira e os repositórios.</p>
 */
public final class GsonFactory {

    private GsonFactory() {
    }

    public static Gson criar() {
        JsonDeserializer<FinancialAsset> deserializer = new JsonDeserializer<FinancialAsset>() {
            @Override
            public FinancialAsset deserialize(JsonElement json, Type typeOfT,
                    JsonDeserializationContext context) {
                JsonObject jsonObject = json.getAsJsonObject();
                int tipo = jsonObject.get("tipo").getAsInt();
                if (tipo == 1) {
                    return context.deserialize(jsonObject, Fii.class);
                } else if (tipo == 2) {
                    return context.deserialize(jsonObject, Criptomoeda.class);
                } else {
                    return context.deserialize(jsonObject, Açao.class);
                }
            }
        };
        return new GsonBuilder()
                .registerTypeAdapter(FinancialAsset.class, deserializer)
                .setPrettyPrinting()
                .create();
    }
}
