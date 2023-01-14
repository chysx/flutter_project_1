import 'package:flutter/material.dart';

class TestProviderViewModel extends ChangeNotifier {
  int count = 0;

  void increment() {
    count++;
    notifyListeners();
  }
}
