import 'package:flutter/material.dart';

void main() {
  runApp(const AppmateSample());
}

class AppmateSample extends StatelessWidget {
  const AppmateSample({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Appmate Sample',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const HomePage(title: 'Appmate Sample'),
    );
  }
}

class HomePage extends StatefulWidget {
  const HomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _items = 0;
  bool storeAccess = true;
  bool isNonConsumablePurchased = false;
  Map<String, bool> isSubscriptionPurchased = {
    "BRONZE": false,
    "SILVER": false,
    "GOLD": false
  };

  void _incrementCounter() {
    if (_items < 5) {
      setState(() {
        _items++;
      });
    } else {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
        content: Text("You are alive more than ever! Can't buy more."),
      ));
    }
  }

  void _decrementCounter() {
    if (_items > 0) {
      setState(() {
        _items--;
      });
    } else {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
        content: Text("You are dead! Need to buy live."),
      ));
    }
  }

  void _purchaseNonConsumable() {
    setState(() {
      isNonConsumablePurchased = true;
    });
  }

  void _purchaseSubscription(String title) {
    setState(() {
      isSubscriptionPurchased.updateAll((title, value) => value = false);
      if (isSubscriptionPurchased[title] != null) {
        isSubscriptionPurchased[title] = true;
      } else {
        isSubscriptionPurchased.addAll({title: true});
      }
    });
  }

  Widget _liveRow(int filled, int total) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        for (int i = 0; i < filled; i++)
          const Icon(
            Icons.favorite,
            color: Colors.red,
          ),
        // Disabled icons
        for (int i = 0; i < total - filled; i++)
          const Icon(
            Icons.favorite,
            color: Colors.grey,
          ),
      ],
    );
  }

  Widget _subscriptionBoard(String title, String price) {
    bool isSubscribed = isSubscriptionPurchased[title] == true;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          children: [
            RichText(
              textAlign: TextAlign.center,
              text: TextSpan(
                style: const TextStyle(color: Colors.black),
                children: <TextSpan>[
                  TextSpan(
                    text: title,
                    style: const TextStyle(
                        fontWeight: FontWeight.w800, fontSize: 12),
                  ),
                  TextSpan(
                    text: "\n\n$price",
                    style: const TextStyle(
                        fontWeight: FontWeight.w500, fontSize: 16),
                  ),
                  const TextSpan(
                    text: " /mo",
                    style: TextStyle(fontSize: 10),
                  ),
                ],
              ),
            ),
            const Padding(padding: EdgeInsets.all(12.0)),
            ElevatedButton(
              onPressed:
                  isSubscribed ? null : () => _purchaseSubscription(title),
              style: ElevatedButton.styleFrom(
                  primary: Colors.green, onPrimary: Colors.white),
              child: isSubscribed ? const Icon(Icons.check) : const Text("GET"),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text(widget.title),
        ),
        body: Padding(
          padding: const EdgeInsets.all(4.0),
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                // Consumable item's indicator
                Card(
                  child: Column(
                    children: [
                      ListTile(
                        title: const Text(
                          "Purchased items",
                          style: TextStyle(fontWeight: FontWeight.bold),
                        ),
                        trailing: TextButton(
                          onPressed: _decrementCounter,
                          child: const Text('Spend'),
                        ),
                      ),
                      const Divider(height: 0, thickness: 1),
                      Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: (_items > 0)
                            ? _liveRow(_items, 5)
                            : const Text("Let's buy consumables"),
                      ),
                    ],
                  ),
                ),

                // Store connection status
                ListTile(
                  title: const Text("Store access"),
                  trailing: storeAccess
                      ? const Icon(
                          Icons.check_circle,
                          color: Colors.green,
                        )
                      : const Icon(
                          Icons.not_interested,
                          color: Colors.red,
                        ),
                ),

                // Product list
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(4.0),
                    child: Column(
                      children: [
                        ListTile(
                          title: const Text("Consumable"),
                          subtitle: const Text(
                              "Consumes after use, buying new ones is necessary."),
                          trailing: ElevatedButton(
                            onPressed: _incrementCounter,
                            style: ElevatedButton.styleFrom(
                                primary: Colors.green, onPrimary: Colors.white),
                            child: const Text(
                              "\$0.99",
                              style: TextStyle(color: Colors.white),
                            ),
                          ),
                        ),
                        const Padding(padding: EdgeInsets.all(10)),
                        ListTile(
                          title: const Text("Non-Consumable"),
                          subtitle:
                              const Text("Purchase once, use continuously."),
                          trailing: ElevatedButton(
                            onPressed: isNonConsumablePurchased
                                ? null
                                : _purchaseNonConsumable,
                            style: ElevatedButton.styleFrom(
                                primary: Colors.green, onPrimary: Colors.white),
                            child: isNonConsumablePurchased
                                ? const Icon(Icons.check)
                                : const Text("\$0.99"),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),

                // Subscription products
                ExpansionTile(
                  title: const ListTile(
                    title: Text("Subscriptions"),
                  ),
                  children: [
                    Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Expanded(
                          child: _subscriptionBoard("BRONZE", "\$5.99"),
                        ),
                        Expanded(
                          child: _subscriptionBoard("SILVER", "\$9.99"),
                        ),
                        Expanded(
                          child: Card(
                            child: _subscriptionBoard("GOLD", "\$12.99"),
                          ),
                        ),
                      ],
                    )
                  ],
                ),
              ],
            ),
          ),
        ));
  }
}
