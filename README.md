# PA_Biblioteca_93246_99197
Trabalho de: João Freire nº93246, Luís Fraga nº99197

Biblioteca de Manipulação de XML
================================

Introdução
-----------

Este projeto fornece uma biblioteca em Kotlin com ferramentas para criar, modificar e gerir documentos XML. Inclui suporte para anotações e a possibilidade de instanciar automáticamente as clases do modelo a partir de objetos para formato XML, assim como um conjunto abrangente de funções para manipular a estrutura XML dinamicamente. O ficheiro XML será contituido por entidade(tag), atributos e texto aninhado em entidade

Funcionalidades
---------------

*   **Anotações para Tradução de objetos para XML**:
    
    *   @Attribute - Marca um elemento para ser adicionada como um atributo à tag.
        
    *   @TagChild - Marca uma elemento para ser adicionada como uma tag filha ao objeto.
        
    *   @ListOfObjects - Marca um elemnto composto por uma lista de objetos, cada um dos quais será traduzido para XML.
        
    *   @XMLString - Marca um elemento para ser alterado para um sufixo de percentagem.

    
        
*   **Manipulação Dinâmica da Estrutura XML**:
    
    *   Adicionar ou remover tags e atributos XML.
        
    *   Alterar nomes de tags e nomes de atributos.
        
    *   Modificar valores de atributos.
        
    *   Formatar a estrutura XML de forma em texto.
        
    *   Guardar a estrutura XML num ficheiro.
        
    *   Recuperar fragmentos XML usando uma abordagem simplificada do XPath.

    *   Converter objetos em entidades(tags) do formato XML.
 

### Pré-requisitos

*   Kotlin 1.4 ou superior
    
* Biliotecas:
    * jetbrains.kotlin.reflect
    * junit.jupiter
* Imports:
```kotlin
import kotlin.reflect.KClass
import java.io.File
import kotlin.reflect.full.*
import kotlin.reflect.full.memberProperties
```
   

        
    

Utilização
----------
### Manipulação das entidades do ficheiro XML
O ficehiro XML será composto por entidades representadas pelos objetos XMLTags. Estes teram como elemntos uma lista que representa as entidades filhas e um map que representa os atributos que a entidade possui. Esta classe tem funções que adicionam, removem e alteram entidades filhas, atributos e o texto aninhado, assim como escrever o objeto em formato XML. Este objetos podem ser manipulados da seguinte forma:

```kotlin
fun exemplo() {
        val curso = XMLTags("curso")
        val fuc = XMLTags("fuc")
        val etcs = XMLTags("ects")
        val nome = XMLTags("nome")


        curso.addChild(fuc)
        curso.addChild(etcs)
        etcs.addChild(nome)
        nome.addText("METI")

        fuc.addAttribute("codigo", "M3234")


        println(curso.prettyPrint())
    }
```
O resultado será:
```
<curso>
 <fuc codigo = "M3234"/>
 <ects>
  <nome>METI</nome>
 </ects>
</curso>
```

### Manipulação do ficheiro XML de forma global
De forma a manipular o ficheiro globalmente, existe a classe XML, que terá a entidade "root", que funcionará como a origem do ficheiro. Desta forma será possivel adicionar, remover e alterar as entidades do ficheiro livremente a partir de um unico objeto utilizando o padrão de desenho visitante(visitor). Isto pode ser realizado da seguinte forma:
```kotlin
fun exemplo() {
        val xml = XML("root")
        val curso = XMLTags("curso")
        val cadeira = XMLTags("cadeira")
        val professor = XMLTags("professor")
        val avaliacao = XMLTags("avaliacao")

        xml.addTagToRoot(curso)
        xml.globalAddTag("curso", cadeira)
        xml.globalAddTag("cadeira", professor)
        xml.globalAddTag("cadeira", avaliacao)

        xml.gobalAddAttribute("cadeira", "nome", "PA")
        xml.gobalAddAttribute("avaliacao", "nome", "quizz")
        xml.gobalAddAttribute("avaliacao", "peso", "40%")

        println(xml.prettyPrint())
    }
```
O resultado originado será:
```
<root>
 <curso>
  <cadeira nome = "PA">
   <professor/>
   <avaliacao nome = "quizz" peso = "40%"/>
  </cadeira>
 </curso>
</root>
```






### Traduzir Objetos para XML

Defina as suas classes através de anotações da seguinte forma :

```kotlin
class ComponenteAvaliacao( @Attribute val nome: String,
                           @Attribute @XMLString(AddPercentage::class) val peso: Int) {

}

class FUC(@Attribute val codigo: String, 
          @TagChild val nome: String,
          @TagChild val etc: String, 
          val obsevacoes: String, 
          @ListOfObjects val avaliaçao: List<ComponenteAvaliacao>) {
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
```

O resultado será o seguinte:

```
<FUC codigo = "M4310">
 <avaliaçao>
  <ComponenteAvaliacao nome = "Quizzes" peso = "20%"/>
  <ComponenteAvaliacao nome = "Projeto" peso = "80%"/>
 </avaliaçao>
 <etc>6.0</etc>
 <nome>Programação Avançada</nome>
</FUC>
```

Contribuições
-------------

