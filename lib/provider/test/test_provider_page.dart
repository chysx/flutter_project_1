import 'package:flutter/material.dart';
import 'package:flutter_project_1/provider/lib/comsumer.dart';
import 'package:flutter_project_1/provider/lib/provider.dart';
import 'package:flutter_project_1/provider/test/test_provider_view_model.dart';

class TestProviderPage extends StatelessWidget {
  const TestProviderPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => TestProviderViewModel(),
      builder: (context) => _buildPage(context),
    );
  }

  Widget _buildPage(BuildContext context) {
    return Consumer<TestProviderViewModel>(
      builder: (context, model) {
        return Scaffold(
          appBar: AppBar(title: const Text('自定义状态管理框架')),
          body: Center(
            child: Text(
              '点击了 ${model.count} 次',
              style: const TextStyle(fontSize: 30.0),
            )
          ),
          floatingActionButton: FloatingActionButton(
            onPressed: () => model.increment(),
            child: const Icon(Icons.add),
          ),
        );
      }
    );
  }
}
