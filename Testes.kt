import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

data class ComponenteAvaliacao( @Attribute val nome: String,
                                @Attribute @XMLString(AddPercentage::class) val peso: Int) {

}

class FUC(@Attribute val codigo: String,
          @TagChild val nome: String,
          @TagChild val etc: String,
          val obsevacoes: String,
          @ListOfObjects val avaliaçao: List<ComponenteAvaliacao>) {
}


class PA{

    @Test
    fun testAddRemoveTag() {
        val root = XML("root")
        val etc = XMLTags("etc")
        val curso = XMLTags("curso")
        val mestrado = XMLTags("mestrado")


        etc.addChild(curso)
        etc.addChild(mestrado)

        root.addTagToRoot(etc)

        Assertions.assertEquals(listOf(curso, mestrado), etc.children)

        println(root.prettyPrint())

        etc.removeChild(mestrado)

        Assertions.assertEquals(listOf(curso), etc.children)

        println("\n\n")

        println(root.prettyPrint())

    }

    @Test
    fun testAddRemoveAttributes() {
        val etc = XMLTags("etc")

        etc.addAttribute("Curso", "METI")
        etc.addAttribute("Ano", "2024")

        println(etc.prettyPrint())

        Assertions.assertEquals(mapOf("Curso" to "METI", "Ano" to "2024"), etc.attributes)

        etc.removeAttribute("Ano")

        println("\n\n" + etc.prettyPrint())

        Assertions.assertEquals(mapOf("Curso" to "METI"), etc.attributes)

        etc.changeAttributeValue("Curso", "LETI")

        println("\n\n" + etc.prettyPrint())

        Assertions.assertEquals(mapOf("Curso" to "LETI"), etc.attributes)
    }

    @Test
    fun testMomAndChildren() {
        val etc = XMLTags("etc")
        val mestrado = XMLTags("mestrado")
        val curso = XMLTags("curso")

        etc.addChild(mestrado)
        etc.addChild(curso)

        Assertions.assertEquals(etc, mestrado.mom)
        Assertions.assertEquals(listOf(mestrado, curso), etc.children)
    }

    @Test
    fun testPrettyPrint() {
        val plano = XML("plano")
        val curso = XMLTags("curso")
        val fuc1 = XMLTags("fuc")
        val nome1 = XMLTags("nome")
        val ects1 = XMLTags("ects")
        val avaliacao1 = XMLTags("avaliacao")
        val componente1= XMLTags("componente")
        val fuc2 = XMLTags("fuc")
        val nome2 = XMLTags("nome")
        val ects2 = XMLTags("ects")
        val avaliacao2 = XMLTags("avaliacao")
        val componente2= XMLTags("componente")
        val componente3= XMLTags("componente")
        val componente4= XMLTags("componente")
        val componente5= XMLTags("componente")


        plano.addTagToRoot(curso)
        plano.addTagToRoot(fuc1)
        curso.addText("Mestrado em Engenharia Informatica")
        fuc1.addAttribute("codigo","M4310")
        fuc1.addChild(nome1)

        nome1.addText("Programacao Avancada")
        fuc1.addChild(ects1)
        ects1.addText("6.0")
        fuc1.addChild(avaliacao1)
        avaliacao1.addChild(componente1)

        componente1.addAttribute("nome","Quizzes")
        componente1.addAttribute("peso","20%")
        avaliacao1.addChild(componente2)
        componente2.addAttribute("nome","Projeto")
        componente2.addAttribute("peso","80%")

        plano.addTagToRoot(fuc2)
        fuc2.addAttribute("codigo","03782")
        fuc2.addChild(nome2)
        nome2.addText("Dissertacao")

        fuc2.addChild(ects2)
        ects2.addText("42.0")
        fuc2.addChild(avaliacao2)

        avaliacao2.addChild(componente3)
        avaliacao2.addChild(componente4)
        avaliacao2.addChild(componente5)

        componente3.addAttribute("nome","Dissertacao")
        componente3.addAttribute("peso","60%")
        componente4.addAttribute("nome","Apresentacao")
        componente4.addAttribute("peso","20%")
        componente5.addAttribute("nome","Discussao")
        componente5.addAttribute("peso","20%")


        val test: String = "<plano>\n" +
                " <curso>Mestrado em Engenharia Informatica</curso>\n" +
                " <fuc codigo = \"M4310\">\n" +
                "  <nome>Programacao Avancada</nome>\n" +
                "  <ects>6.0</ects>\n" +
                "  <avaliacao>\n" +
                "   <componente nome = \"Quizzes\" peso = \"20%\"/>\n" +
                "   <componente nome = \"Projeto\" peso = \"80%\"/>\n" +
                "  </avaliacao>\n" +
                " </fuc>\n" +
                " <fuc codigo = \"03782\">\n" +
                "  <nome>Dissertacao</nome>\n" +
                "  <ects>42.0</ects>\n" +
                "  <avaliacao>\n" +
                "   <componente nome = \"Dissertacao\" peso = \"60%\"/>\n" +
                "   <componente nome = \"Apresentacao\" peso = \"20%\"/>\n" +
                "   <componente nome = \"Discussao\" peso = \"20%\"/>\n" +
                "  </avaliacao>\n" +
                " </fuc>\n" +
                "</plano>"

        println(plano.prettyPrint())
        Assertions.assertEquals(test, plano.prettyPrint())
        plano.writeToFile("XML")
    }

    @Test
    fun testGlobalAddRemoveAttribute() {
        val xml = XML("root")
        val curso = XMLTags("curso")
        val fuc = XMLTags("fuc")
        val nome = XMLTags("nome")
        val etcs = XMLTags("ects")
        val avaliacao = XMLTags("avaliacao")
        val componente= XMLTags("componente")
        val cadeira = XMLTags("cadeira")
        val professor = XMLTags("professor")

        curso.addChild(fuc)
        curso.addChild(nome)
        nome.addText("METI")
        curso.addChild(etcs)
        etcs.addChild(avaliacao)
        etcs.addChild(componente)
        curso.addChild(cadeira)
        cadeira.addChild(professor)

        xml.addTagToRoot(curso)

        xml.gobalAddAttribute("componente", "nome", "quizzes")
        xml.gobalAddAttribute("componente", "peso", "40%")
        xml.gobalAddAttribute("cadeira", "nome", "PA")

        Assertions.assertEquals(mapOf("nome" to "quizzes", "peso" to "40%"), componente.attributes)
        Assertions.assertEquals(mapOf("nome" to "PA"), cadeira.attributes)

        println(xml.prettyPrint())

        xml.globalRemoveAttribute("cadeira", "nome")
        xml.globalRemoveAttribute("componente", "peso")

        Assertions.assertEquals(mapOf("nome" to "quizzes"), componente.attributes)
        Assertions.assertEquals(emptyMap<String, String>(), cadeira.attributes)

        println("\n\n" + xml.prettyPrint())


    }



    @Test
    fun testGlobalChangeTagName(){

        val xml = XML("root")
        val curso = XMLTags("curso")
        val fuc = XMLTags("fuc")
        val nome = XMLTags("nome")
        val etcs = XMLTags("ects")
        val avaliacao = XMLTags("avaliacao")
        val componente= XMLTags("componente")
        val cadeira = XMLTags("cadeira")
        val professor = XMLTags("professor")

        curso.addChild(fuc)
        curso.addChild(nome)
        nome.addText("METI")
        curso.addChild(etcs)
        etcs.addChild(avaliacao)
        etcs.addChild(componente)
        curso.addChild(cadeira)
        cadeira.addChild(professor)

        xml.addTagToRoot(curso)

        xml.gobalAddAttribute("componente", "nome", "quizzes")
        xml.gobalAddAttribute("componente", "peso", "40%")
        xml.gobalAddAttribute("cadeira", "nome", "PA")

        println(xml.prettyPrint())

        xml.globalChangeTagName("cadeira", "disciplina")
        xml.globalChangeTagName("root", "xml")

        Assertions.assertEquals("disciplina", cadeira.name)
        Assertions.assertEquals("xml", xml.root.name)

        println("\n\n" + xml.prettyPrint())

        xml.globalChangeTagName("disciplina", "NovaCadeira")


        Assertions.assertEquals("NovaCadeira", cadeira.name)

        println("\n\n" + xml.prettyPrint())

    }

    @Test
    fun testGlobalChangeAttribute(){
        val xml = XML("root")
        val curso = XMLTags("curso")
        val fuc = XMLTags("fuc")
        val nome = XMLTags("nome")
        val etcs = XMLTags("ects")
        val avaliacao = XMLTags("avaliacao")
        val componente= XMLTags("componente")
        val cadeira = XMLTags("cadeira")
        val professor = XMLTags("professor")

        curso.addChild(fuc)
        curso.addChild(nome)
        nome.addText("METI")
        curso.addChild(etcs)
        etcs.addChild(avaliacao)
        etcs.addChild(componente)
        curso.addChild(cadeira)
        cadeira.addChild(professor)

        xml.addTagToRoot(curso)

        xml.gobalAddAttribute("componente", "nome", "quizzes")
        xml.gobalAddAttribute("componente", "peso", "40%")
        xml.gobalAddAttribute("cadeira", "nome", "PA")

        Assertions.assertEquals(mapOf("nome" to "quizzes", "peso" to "40%"), componente.attributes)
        Assertions.assertEquals(mapOf("nome" to "PA"), cadeira.attributes)

        println(xml.prettyPrint())

        xml.globalChangeAttributeValue("cadeira", "nome", "programação avançada")
        xml.globalChangeAttributeValue("componente", "peso", "100%")

        println("\n\n" + xml.prettyPrint())

        Assertions.assertEquals(mapOf("nome" to "quizzes", "peso" to "100%"), componente.attributes)
        Assertions.assertEquals(mapOf("nome" to "programação avançada"), cadeira.attributes)

        xml.globalChangeAttributeName("cadeira", "nome", "UC")

        println("\n\n" + xml.prettyPrint())

        Assertions.assertEquals(mapOf("UC" to "programação avançada"), cadeira.attributes)

    }


    @Test
    fun testGlobalAddRemoveTag(){
        val xml = XML("root")
        val curso = XMLTags("curso")
        val fuc = XMLTags("fuc")
        val nome = XMLTags("nome")
        val etcs = XMLTags("etcs")
        val avaliacao = XMLTags("avaliacao")
        val componente= XMLTags("componente")
        val cadeira = XMLTags("cadeira")
        val professor = XMLTags("professor")

        curso.addChild(fuc)
        curso.addChild(nome)
        nome.addText("METI")
        curso.addChild(etcs)
        curso.addChild(cadeira)

        xml.addTagToRoot(curso)

        println(xml.prettyPrint())

        Assertions.assertEquals(emptyList<XMLTags>(), etcs.children)
        Assertions.assertEquals(emptyList<XMLTags>(), cadeira.children)

        xml.globalAddTag("etcs", avaliacao)
        xml.globalAddTag("etcs", componente)
        xml.globalAddTag("cadeira", professor)

        println("\n\n" + xml.prettyPrint())


        Assertions.assertEquals(listOf(avaliacao, componente), etcs.children)
        Assertions.assertEquals(listOf(professor), cadeira.children)







    }


    @Test
    fun testXpath() {
        val xml = XML("root")
        val curso = XMLTags("curso")
        val exemplo = XMLTags("exemplo")
        val teste = XMLTags("exemplo")
        xml.addTagToRoot(curso)
        curso.addChild(exemplo)
        curso.addChild(teste)
        exemplo.addAttribute("Teste", "fixe")
        teste.addAttribute("Teste", "buedafixe")

        val test : String = "<exemplo Teste = \"fixe\"/>\n<exemplo Teste = \"buedafixe\"/>\n"

        println(xml.prettyPrint())

        println("\n\n" + xml.microXpath("curso/exemplo"))

        Assertions.assertEquals(test, xml.microXpath("curso/exemplo"))
    }

    @Test
    fun testCreatedObjects(){
        val componente = ComponenteAvaliacao("PA", 40)
        val quizzes = ComponenteAvaliacao("Quizzes", 20)
        val projeto = ComponenteAvaliacao("Projeto", 80)
        val list = listOf(quizzes,projeto)
        val f = FUC("M4310", "Programação Avançada", "6.0", "la la...", list)
        val tag = translate(f)
        println(tag.prettyPrint())

    }





}