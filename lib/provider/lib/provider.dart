import 'package:flutter/material.dart';

class ChangeNotifierProvider<T extends ChangeNotifier> extends StatelessWidget {
  const ChangeNotifierProvider({
    Key? key,
    required this.create,
    this.builder,
    this.child,
  }) : super(key: key);

  final T Function(BuildContext context) create;

  final Widget Function(BuildContext context)? builder;
  final Widget? child;

  @override
  Widget build(BuildContext context) {
    assert(
      builder != null || child != null,
      '$runtimeType  必须指定一个子元素',
    );

    return ProviderInherited(
      create: create,
      child: builder != null
          ? Builder(builder: (context) => builder!(context))
          : child!,
    );
  }
}

class ProviderInherited<T extends ChangeNotifier> extends InheritedWidget {
  const ProviderInherited({
    Key? key,
    required Widget child,
    required this.create,
  }) : super(key: key, child: child);

  final T Function(BuildContext context) create;

  @override
  bool updateShouldNotify(InheritedWidget oldWidget) => false;

  @override
  InheritedElement createElement() => ProviderInheritedElement(this);
}

class ProviderInheritedElement<T extends ChangeNotifier> extends InheritedElement {
  ProviderInheritedElement(ProviderInherited<T> widget) : super(widget);

  bool _firstBuild = true;
  bool _shouldNotify = false;
  late T _value;
  late void Function() _callBack;

  T get value => _value;

  @override
  void performRebuild() {
    if (_firstBuild) {
      _firstBuild = false;
      _value = (widget as ProviderInherited<T>).create(this);

      _value.addListener(_callBack = () {
        _shouldNotify = true;
        markNeedsBuild();
      });
    }
    super.performRebuild();
  }

  @override
  Widget build() {
    if (_shouldNotify) {
      _shouldNotify = false;
      notifyClients(widget as ProviderInherited<T>);
    }
    return super.build();
  }

  @override
  void notifyDependent(covariant InheritedWidget oldWidget, Element dependent) {
    dependent.markNeedsBuild();
  }

  @override
  void unmount() {
    _value.removeListener(_callBack);
    _value.dispose();
    super.unmount();
  }
}
