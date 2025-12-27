import final.InvoiceTextGenerator
import final.Order
import final.Product
import final.ShipmentItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InvoiceTextGeneratorTest {

    @Test
    fun `generate should succeed for order with multiple products`() {
        val products = mapOf(
            "BOOK" to Product("Programming Book", "standard"),
            "TABLE" to Product("Long Table", "standard"),
            "LAPTOP" to Product("Premium Laptop", "express"),
            "MONITOR" to Product("4K Monitor", "express")
        )

        val order = Order(
            customerName = "Acme Corp",
            shipmentItems = listOf(
                ShipmentItem("BOOK", 5, 4.9),
                ShipmentItem("TABLE", 2, 5.1),
                ShipmentItem("LAPTOP", 3, 2.9),
                ShipmentItem("MONITOR", 2, 3.1),
            )
        )
        val generator = InvoiceTextGenerator(order, products)
        val actualInvoiceText = generator.generate()
        val expected =
            """Shipping Invoice for Acme Corp
  Programming Book: ${'$'}5.00 (5 items, 4.9kg)
  Long Table: ${'$'}5.09 (2 items, 5.1kg)
  Premium Laptop: ${'$'}16.50 (3 items, 2.9kg)
  4K Monitor: ${'$'}15.25 (2 items, 3.1kg)
Total shipping cost is ${'$'}41.84
You earned 5 loyalty points
"""
        assertEquals(expected, actualInvoiceText)
    }

}
