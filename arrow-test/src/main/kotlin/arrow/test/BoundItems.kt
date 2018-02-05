package arrow.test

import arrow.bounded
import arrow.lenses
import arrow.optics.lensing
import arrow.optics.nullable

@bounded
@lenses
data class Street(val number: Int, val name: String)

@bounded
@lenses
data class Address(val city: String, val street: Street)

@bounded
@lenses
data class Company(val name: String, val address: Address)

@bounded
@lenses
data class Employee(val name: String, val company: Company?)

fun main(args: Array<String>) {

    val employee = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))

    val newEmployee = employee.lensing().company.nullable.address.street.name.modify { it.capitalize() }

    println(newEmployee)

}