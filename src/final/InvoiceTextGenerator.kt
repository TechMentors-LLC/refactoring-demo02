package final


data class ShipmentItem(
    val productID: String,
    val quantity: Int,
    val weight: Double
)

data class Order(
    val customerName: String,
    val shipmentItems: List<ShipmentItem>
)

data class Product(
    val name: String,
    val shippingMethod: String
)

class InvoiceTextGenerator(val order: Order, val products: Map<String, Product>) {

    fun generate(): String {
        var totalCost = 0
        var loyaltyPoints = 0
        var result = "Shipping Invoice for ${order.customerName}\n"

        for (item in order.shipmentItems) {
            val product = products[item.productID]!!
            var thisCost = 0
            when (product.shippingMethod) {
                "standard" -> {
                    thisCost = 500
                    if (item.weight > 5.0) {
                        thisCost += (100 * (item.weight - 5.0)).toInt()
                    }
                }

                "express" -> {
                    thisCost = 1200
                    if (item.weight > 3.0) {
                        thisCost += (250 * (item.weight - 3.0)).toInt()
                    }

                    thisCost += 150 * item.quantity
                }

                else -> {
                    throw kotlin.IllegalArgumentException("unknown shipping method: ${product.shippingMethod}")
                }
            }

            loyaltyPoints += kotlin.comparisons.maxOf(item.quantity - 2, 0)
            if ("express" == product.shippingMethod) {
                loyaltyPoints += item.quantity / 3
            }

            result +=
                "  ${product.name}: ${formatCurrency(thisCost)} " +
                        "(${item.quantity} items, ${item.weight}kg)\n"

            totalCost += thisCost
        }

        result += "Total shipping cost is ${formatCurrency(totalCost)}\n"
        result += "You earned $loyaltyPoints loyalty points\n"

        return result
    }

    fun formatCurrency(amountInCents: Int): String {
        val amountInDollars = amountInCents / 100.0
        return "$%.2f".format(amountInDollars)
    }
}

