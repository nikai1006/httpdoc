package io.httpdoc.gen;

import io.httpdoc.core.Document;
import io.httpdoc.core.generation.Generation;
import io.httpdoc.core.generation.Generator;
import io.httpdoc.core.strategy.OverrideStrategy;
import io.httpdoc.jackson.deserialization.YamlDeserializer;
import io.httpdoc.jestful.client.JestfulMergedGenerator;
import io.httpdoc.test.ProductAPI;
import org.junit.Test;
import org.qfox.jestful.client.Client;

import java.io.IOException;
import java.net.URL;

/**
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-05-16 13:18
 **/
public class Generate {

    public static void main(String... args) throws IOException {
        Document document = Document.from(new URL("http://localhost:8080/httpdoc-sample/httpdoc.yaml"), new YamlDeserializer());
        Generation generation = new Generation(document);
        generation.setDirectory(System.getProperty("user.dir") + "\\httpdoc-sample\\src\\test\\java");
        generation.setPkg("io.httpdoc.gen");
        generation.setPkgForced(false);
        generation.setStrategy(new OverrideStrategy());
//        generation.setSupplier(new RetrofitSupplier());
//        Generator generator = new RetrofitMergedGenerator();
//                .include(RetrofitCallGenerator.class)
//                .include(RetrofitRxJavaGenerator.class)
//                .include(RetrofitJava8Generator.class)
//                .include(RetrofitGuavaGenerator.class);

        Generator generator = new JestfulMergedGenerator();
//                .exclude(JestfulCallbackGenerator.class);
//                .include(JestfulClientLambdaGenerator.class)
//                .include(JestfulClientFutureGenerator.class)
//                .include(JestfulClientGuavaGenerator.class)
//                .include(JestfulClientJava8Generator.class)
//                .include(JestfulClientMessageGenerator.class)
//                .include(JestfulClientEntityGenerator.class)
//                .include(JestfulClientHeaderGenerator.class)
//                .include(JestfulClientObservableGenerator.class);

        generator.generate(generation);
    }

    @Test
    public void test() throws Exception {
        ProductAPI productAPI = Client.builder().setEndpoint(new URL("http://localhost:8080/httpdoc-sample")).build().create(ProductAPI.class);
        productAPI.update(
                12L,
                "sdf",
                null
        );
    }

}
