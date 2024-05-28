import kotlin.reflect.KClass
import java.io.File
import kotlin.reflect.full.*
import kotlin.reflect.full.memberProperties

//A anotação é utilizada quando queremos que um elemnto de um objeto seja adicionado à tag deste, como atributo
@Target(AnnotationTarget.PROPERTY)
annotation class Attribute

//A anotação indica que o argumento será uma tag filha da tag do objeto
@Target(AnnotationTarget.PROPERTY)
annotation class TagChild

//A anotação é usada quando o argumento é uma lista de objetos,
// assim volta a chamar a função translate em cada um dos objetos
@Target(AnnotationTarget.PROPERTY)
annotation class ListOfObjects

//Usada para transformar um argumento numa stirng percentual
@Target(AnnotationTarget.PROPERTY)
annotation class XMLString( val value : KClass<out AddPercentage>)

//USada para adicionar texto à tag do objeto
@Target(AnnotationTarget.CLASS)
annotation class Text( val value : String)


fun translate(o: Any) : XMLTags {
    val clazz = o::class


    val tag = XMLTags(clazz.simpleName.toString())
    if (clazz.hasAnnotation<Text>()){
        tag.addText(clazz.findAnnotation<Text>()!!.value)
    }
    clazz.memberProperties.forEach {
        when {
            it.hasAnnotation<XMLString>() ->{
                val per = object : AddPercentage {}
                tag.addAttribute(it.name, per.addPercentage(it.call(o)!!))
            }


            it.hasAnnotation<Attribute>() -> {
                tag.addAttribute(it.name, it.call(o).toString())
            }

            it.hasAnnotation<TagChild>() -> {

                val childTag = XMLTags(it.name)
                childTag.addText(it.call(o).toString())
                tag.addChild(childTag)
            }

            it.hasAnnotation<ListOfObjects>() -> {
                val childTag = XMLTags(it.name)
                val list = it.call(o) as List<*>
                list.forEach { i -> childTag.addChild(translate(i!!)) }
                tag.addChild(childTag)
            }

        }
    }
    return tag

}


interface AddPercentage{
    fun addPercentage (value : Any) : String{
        return "$value%"
    }
}

interface Adapter{
    fun adapter(value : XMLTags){

    }
}

//Classe que constroi o objeto XML que é utilizado para adicionar, retirar ou alterar
//Valores de forma global
class XML(var name: String) {
    val root = XMLTags(name)

    fun addTagToRoot(xmlTag: XMLTags) {
        root.addChild(xmlTag)
    }

    fun removeTagFromRoot(xmlTag: XMLTags) {
        root.removeChild(xmlTag)
    }

    fun rootChildren(): List<XMLTags> {
        return root.children

    }

    //Adiciona um atributo de forma global a uma tag indicada pelo nome
    fun gobalAddAttribute(tagName: String, attributeName: String, attributeValue: String) {
        var exists = false
        root.accept {
            if (it.name == tagName) {
                it.addAttribute(attributeName, attributeValue)
                exists=true
                false
            } else true
        }
        if (!exists)
            println("A entidade não existe")
    }

    //Altera o nome de uma tag de forma global indicando o nome antigo
    fun globalChangeTagName(oldName: String, newName: String) {
        var exists = false
        root.accept {
            if (it.name == oldName) {
                it.name = newName
                exists=true
                false
            } else true
        }
        if(!exists)
            println("A entidade não existe")

    }


    //Altera o nome de um atributo de uma determinada tag de forma global indicando o nome da tag
    fun globalChangeAttributeName(tagName: String, oldAttributeName: String, newAttributeName: String) {
        var exists = false
        root.accept {
            if (it.name == tagName) {
                exists=true
                val value: String = it.attributes[oldAttributeName]!!
                it.removeAttribute(oldAttributeName)
                it.addAttribute(newAttributeName, value)
                false
            } else true
        }
        if (!exists)
            println("A entidade não existe")
    }

    //Adciona uma tag com uma filha ao ser indicado o nome a tag mãe
    fun globalAddTag(momTag : String, newTag : XMLTags){
        var exists = false
        root.accept {
            if (it.name == momTag) {
                exists=true
                it.addChild(newTag)
                false
            } else true
        }
        if (!exists)
            println("A entidade mãe não existe")
    }


    fun globalChangeAttributeValue(tagName: String, attributeName: String, newAValue: String) {
        var exists = false
        root.accept {
            if (it.name == tagName) {
                exists=true
                it.changeAttributeValue(attributeName,newAValue)
                false
            } else true
        }
        if (!exists)
            println("A entidade não existe")
    }

    //Remove uma tag de forma global sneod indicado o nome
    fun globalRemoveTag(tagName: String) {
        var tag: XMLTags? = null
        var exists = false
        root.accept {
            if (it.name == tagName) {
                tag = it
                exists=true
                false
            } else true
        }
        if(!exists)
            println("A entidade não existe")
        else
            tag?.mom?.removeChild(tag!!)
    }

    //Remove um atributo de forma global a uma tag indicada pelo nome
    fun globalRemoveAttribute(tagName: String, attributeName: String) {
        var exists = false
        root.accept {
            if (it.name == tagName) {
                it.removeAttribute(attributeName)
                exists=true
                false
            } else true
        }
        if (!exists)
            println("A entidade não existe")
    }

    //Cria a string do documento XML
    fun prettyPrint(): String {
        val script: String = root.prettyPrint()
        return script
    }

    //Cria o ficheiro XML a partir da String do prettyPrint
    fun writeToFile(filename: String) {
        File(filename).writeText(prettyPrint())
    }

    //Devolve a String de um fragmento do XML, sendo indicado o caminho
    //Na String são demonstradas as tags e os atributos destas se existirem
    fun microXpath(path: String): String? {
        val auxList = mutableListOf<XMLTags>(root)
        val finalList = mutableListOf<XMLTags>(root)
        val xpath = path.split("/")
        for (i in 0..<xpath.size) {
            auxList.clear()
            auxList.addAll(finalList)
            finalList.clear()
            auxList.forEach {
                it.children.forEach() {
                    if (it.name == xpath[i]) {
                        finalList.add(it)
                    }
                }
            }
        }
        var script: String? = ""
        finalList.forEach {
            script += "<${it.name}"

            it.attributes.forEach { script += " ${it.key} = \"${it.value}\"" }

            script += "/>\n"
        }

        return script
    }

}

//Cria um objeto XMLTags que é utilizado como as entidades no XML, contem uma lista children com os filhos
//Assim como um atributo mom que indica o parente da tag. Também tem um mapa como atributo que representa os atributos da tag
class XMLTags(var name: String) {
    val children = mutableListOf<XMLTags>()
    val attributes = mutableMapOf<String, String>()
    var mom: XMLTags? = null
    var text: String? = null

    fun addChild(child: XMLTags) {
        if (children.contains(child) || child.mom!=null)
            println("Esta entidade já faz parte do ficheiro")
        else {
            children.add(child)
            child.mom = this
        }
    }

    //retira a entidade child dos filhos desta tag, e retira todas os filhos da entidade child recursivamente
    fun removeChild(child: XMLTags) {
        children.remove(child)
        child.mom = null
        if (child.children.isNotEmpty()) {
            child.children.forEach { removeChild(it) }
        }
    }

    fun addAttribute(name: String, value: String) {
        if (attributes.containsKey(name))
            println("O atributo já existe nesta entidade")
        else
            attributes[name] = value
    }

    fun removeAttribute(name: String) {
        if (!attributes.containsKey(name))
            println("O atributo não existe nesta entidade")
        else
            attributes.remove(name)
    }

    fun changeAttributeValue(name: String, value: String) {
        if (!attributes.containsKey(name))
            println("O atributo não existe nesta entidade")
        else
            attributes.replace(name, value)
    }

    fun addText(newText: String) {
        text = newText
    }

    //A função accept percorre a entidade e todos os filhos desta
    fun accept(visitor: (XMLTags) -> Boolean) {
        if (visitor(this))
            children.forEach {
                it.accept(visitor)
            }
    }

    //A função deph verifica quantos parentes tem a entidade até chgar a uma entidade que não tenha parente
    fun depth(): Int {
        var count = 0
        var i = mom
        while (i != null) {
            count++
            i = i.mom
        }
        return count
    }

    //O prettyPrint constroi e devolve uma String do ficheiro XML a partir da entidade
    //A String é contuida consuante o numero de filho e se é acomppanhada ppor texto
    fun prettyPrint(): String {
        val indent = " ".repeat(depth())
        var script = indent + "<${name}"



        attributes.forEach { script += " ${it.key} = \"${it.value}\"" }

        if (children.isNotEmpty()) {
            script += ">"
            if (text != null) {
                script += text + "</${name}>"
            }

            children.forEach { script += "\n${it.prettyPrint()}" }
            script += "\n${indent}</${name}>"
        } else {
            if (text != null) {
                script += ">" + text + "</${name}>"
            }else {
                script += "/>"
            }
        }

        return script

    }



}







