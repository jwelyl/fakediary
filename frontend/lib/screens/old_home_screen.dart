import 'package:flutter/material.dart';
import 'package:frontend/screens/home_circlemenu.dart';
import 'exchange_animation/gift_card_list_page.dart';
// import 'package:frontend/screens/diary-list-filter.dart';
import 'package:frontend/screens/old_menu_screen.dart';

class OldHomeScreen extends StatefulWidget {
  const OldHomeScreen({super.key});

  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<OldHomeScreen> {
  @override
  Widget build(BuildContext context) {
    return Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            fit: BoxFit.cover,
            image: AssetImage('assets/img/bg_moon.png'),
          ),
        ),
        child: Scaffold(
            backgroundColor: Colors.transparent,
            appBar: AppBar(
              automaticallyImplyLeading: false,
              backgroundColor: Colors.transparent,
              elevation: 0,
              toolbarHeight: MediaQuery.of(context).size.height * 0.1183,
              actions: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Padding(
                      padding: EdgeInsets.symmetric(
                        horizontal: 30,
                      ),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          Image(
                            image: AssetImage('assets/img/icon_alarm.png'),
                            width: 45, // set the desired width
                            height: 45, // set the desired height
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ],
            ),
            body: Center(
              child: Column(
                children: [
                  Flexible(flex: 2, child: Container()),
                  Flexible(
                      flex: 1,
                      child: IconButton(
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) => MenuScreen()),
                          );
                        },
                        icon: Image.asset(
                          'assets/img/icon_menu.png',
                        ),
                        iconSize: 80,
                      )),
                  Flexible(
                      flex: 1,
                      child: IconButton(
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) => GiftCardListPage()),
                          );
                        },
                        icon: Image.asset(
                          'assets/img/icon_menu.png',
                        ),
                        iconSize: 80,
                      )),                  Flexible(
                      flex: 1,
                      child: IconButton(
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) => HomeScreen()),
                          );
                        },
                        icon: Image.asset(
                          'assets/img/icon_menu.png',
                        ),
                        iconSize: 80,
                      )),

                ],
              ),
            )));
  }
}
