package de.ddb.next

class Items3dController {
    def index() {
        def id = params.id,
        kanne = false,
        draisine = false,
        elefant = false,
        nofretete = false,
        aegyptische_Statue3 = false

        if (id == "kanne") {
            kanne = true
        } else
        if (id == "draisine") {
            draisine = true
        } else
        if (id == "elefant") {
            elefant = true
        } else
        if (id == "nofretete") {
            nofretete = true
        } else
        if (id == "aegyptische_Statue3") {
            aegyptische_Statue3 = true
        }
        render(view: "items3d", model: [kanne: kanne, draisine: draisine, elefant: elefant, nofretete: nofretete, aegyptische_Statue3: aegyptische_Statue3, id: id])
    }
}
