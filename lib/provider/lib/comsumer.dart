import 'package:flutter/material.dart';
import 'package:flutter_project_1/provider/lib/util.dart';

class Consumer<T extends ChangeNotifier> extends StatelessWidget {
  const Consumer({Key? key, required this.builder,}) : super(key: key);

  final Widget Function(BuildContext context, T model) builder;

  @override
  Widget build(BuildContext context) {
    return builder(context, ProviderUtil.of<T>(context));
  }
}
