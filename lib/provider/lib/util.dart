import 'package:flutter/material.dart';

import 'provider.dart';

class ProviderUtil {

  static T of<T extends ChangeNotifier>(BuildContext context) {
    var element = _getInheritedElement<T>(context);
    context.dependOnInheritedElement(element);
    return element.value;
  }

  static ProviderInheritedElement<T>
      _getInheritedElement<T extends ChangeNotifier>(BuildContext context) {
    var inheritedElement = context
            .getElementForInheritedWidgetOfExactType<ProviderInherited<T>>()
        as ProviderInheritedElement<T>?;

    if (inheritedElement == null) {
      throw Exception('没有找到ProviderInheritedElement');
    }
    return inheritedElement;
  }
}
