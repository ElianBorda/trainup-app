package ar.com.unq.eis.trainup.services.impl

import ar.com.unq.eis.trainup.controller.Exceptions.RutinaException
import ar.com.unq.eis.trainup.dao.EjercicioDAO
import ar.com.unq.eis.trainup.dao.RutinaDAO
import ar.com.unq.eis.trainup.model.Ejercicio
import ar.com.unq.eis.trainup.model.Rutina
import ar.com.unq.eis.trainup.services.RutinaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional
class RutinaServiceImpl : RutinaService {

    @Autowired
    lateinit var rutinaDAO: RutinaDAO

    @Autowired
    lateinit var ejercicioDAO: EjercicioDAO


    override fun crearRutina(rutina: Rutina): Rutina {
        return try {
            rutinaDAO.save(rutina)
        } catch (e: Exception) {
            throw RuntimeException("Error al crear la rutina: ${e.message}")
        }
    }

    override fun obtenerRutinas(): List<Rutina> {
        return try {
            rutinaDAO.findAll()
        } catch (e: Exception) {
            throw RuntimeException("Error al obtener la lista de rutinas: ${e.message}")
        }
    }

    override fun obtenerRutinaPorId(id: String): Rutina {
        return try {
            val rutina = rutinaDAO.findById(id)
            if (rutina.isPresent) {
                rutina.get()
            } else {
                throw NoSuchElementException("No se encontró la rutina con id: $id")
            }
        } catch (e: NoSuchElementException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("Error al buscar la rutina por id: ${e.message}")
        }
    }

    override fun actualizarRutina(rutinaActualizada: Rutina): Rutina {

        val id = rutinaActualizada.id ?: throw RutinaException("el id no puede ser null")
        val rutinaExistente = rutinaDAO.findById(id)
            .getOrElse { throw NoSuchElementException("No se encontró la rutina con id: $id") }

        rutinaExistente.nombre = rutinaActualizada.nombre
        rutinaExistente.descripcion = rutinaActualizada.descripcion
        rutinaExistente.categoria = rutinaActualizada.categoria
        rutinaExistente.dificultad = rutinaActualizada.dificultad

        return rutinaDAO.save(rutinaExistente);
    }

    override fun eliminarRutina(id: String) {
        try {
            if (rutinaDAO.existsById(id)) {
                rutinaDAO.deleteById(id)
            } else {
                throw NoSuchElementException("No se encontró la rutina con id: $id")
            }
        } catch (e: NoSuchElementException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("Error al eliminar la rutina: ${e.message}")
        }
    }

    override fun agregarEjercicio(id: String, ejercicio: Ejercicio): Rutina {
        return try {
            val rutinaExistente = rutinaDAO.findById(id)
            if (rutinaExistente.isPresent) {
                val rutina = rutinaExistente.get()
                rutina.agregarEjercicio(ejercicioDAO.save(ejercicio))
                rutinaDAO.save(rutina)
            } else {
                throw NoSuchElementException("No se encontró la rutina con id: $id")
            }
        } catch (e: NoSuchElementException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("Error al agregar ejercicio a la rutina: ${e.message}")
        }
    }

    override fun eliminarEjercicio(id: String, idEj: String): Rutina {
        return try {
            val rutinaExistente = rutinaDAO.findById(id)
            if (rutinaExistente.isPresent) {
                val rutina = rutinaExistente.get()
                rutina.eliminarEjercicio(idEj)
                rutinaDAO.save(rutina)
            } else {
                throw NoSuchElementException("No se encontró el ejercicio con id: $idEj")
            }
        } catch (e: NoSuchElementException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("Error al eliminar ejercicio de la rutina: ${e.message}")
        }
    }

    override fun obtenerRutinasPorCategoria(categoria: String): List<Rutina> {

        return try {
            this.rutinaDAO.findByCategoria(categoria)
        } catch (e: Exception) {
            throw RuntimeException("Error al obtener la lista de rutinas por categoría: ${e.message}")
        }

    }

    override fun buscarRutinas(nombre: String, dificultad: String?): List<Rutina> {
        return try {
            if (!dificultad.isNullOrBlank()) {
                this.rutinaDAO.findByNombreContainingIgnoreCaseAndDificultad(nombre, dificultad)
            } else {
                this.rutinaDAO.findByNombreContainingIgnoreCase(nombre)
            }

        } catch (e: DataAccessException) {
            throw RutinaException("Error al realizar la busqueda en la base de datos")
        }
    }
}