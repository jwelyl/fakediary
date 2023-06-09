import 'package:flutter/material.dart';
import 'package:frontend/screens/card_create.dart';
import 'package:frontend/screens/card_list.dart';
import 'package:frontend/screens/diary_create_cards.dart';
import 'package:gradient_borders/box_borders/gradient_box_border.dart';
import 'package:kakao_flutter_sdk_share/kakao_flutter_sdk_share.dart';
import 'package:lottie/lottie.dart';
import 'dart:math' as math;

import 'home_circlemenu.dart';

class CardResult extends StatefulWidget {
  final Map<String, dynamic> card;

  const CardResult({Key? key, required this.card}) : super(key: key);

  @override
  State<CardResult> createState() => _CardResultState();
}

Widget _buttonList(BuildContext context) {
  return Container(
    width: 350,
    height: 50,
    decoration: BoxDecoration(
        border: GradientBoxBorder(
            gradient: LinearGradient(
              colors: [
                Color(0xff79F1A4),
                Color(0xff0E5CAD),
              ],
            ),
            width: 2),
        borderRadius: BorderRadius.circular(25)),
    child: ElevatedButton(
        onPressed: () {
          Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => const DiaryCreateCards(),
              ));
        },
        style: ElevatedButton.styleFrom(
            backgroundColor: Colors.transparent,
            shadowColor: Colors.transparent,
            elevation: 0.0,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(30.0),
            )),
        child: Text(
          '일기 쓰러 가기',
          style: TextStyle(
              color: Colors.white, fontSize: 14, fontWeight: FontWeight.w700),
        )),
  );
}

class _CardResultState extends State<CardResult>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _animation;
  bool _isFrontVisible = true; //앞뒤 구분
  double opacityLevel = 0.0;

  @override
  void initState() {
    super.initState();
    _controller =
        AnimationController(vsync: this, duration: const Duration(seconds: 1));
    _animation = Tween<double>(begin: 0, end: 1).animate(_controller)
      ..addListener(() {
        setState(() {});
      });
    Future.delayed(const Duration(milliseconds: 500), () {
      setState(() {
        opacityLevel = 1.0;
      });
    });
  }

  DefaultTemplate _getTemplate() {
    String title = widget.card['createdAt'];
    Uri imageLink = Uri.parse(widget.card['cardImageUrl']);
    Link link = Link(
        webUrl: Uri.parse("https://developers.kakao.com"),
        mobileWebUrl: Uri.parse("https://developers.kakao.com"));

    Content content = Content(title: title, imageUrl: imageLink, link: link);

    FeedTemplate template = FeedTemplate(
      content: content,
      buttonTitle: '오늘 기억의 조각 만들기',
    );

    return template;
  }

  void shareMyCode() async {
    try {
      DefaultTemplate template = _getTemplate();
      Uri uri = await ShareClient.instance.shareDefault(template: template);
      await ShareClient.instance.launchKakaoTalk(uri);
      print('카카오톡 공유 완료');
    } catch (error) {
      print('kakao error : ${error.toString()}');
    }
  }

  CheckKakao() async {
    bool isKakaoTalkSharingAvailable =
    await ShareClient.instance.isKakaoTalkSharingAvailable();

    if (isKakaoTalkSharingAvailable) {
      shareMyCode();
    } else {
      try {
        DefaultTemplate template = _getTemplate();
        Uri shareUrl =
        await WebSharerClient.instance.makeDefaultUrl(template: template);
        await launchBrowserTab(shareUrl, popupOpen: true);
        print('NoKakao');
      } catch (error) {
        print('kakao no install error : ${error.toString()}');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async {
        Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const HomeScreen(),
            ));
        return false;
      },
      child: Container(
        decoration: BoxDecoration(
          // gradient: LinearGradient(
          //   begin: Alignment.topLeft,
          //   end: Alignment.bottomRight,
          //   colors: [Color(0xff142e34), Color(0xff4F4662)],
          //   stops: [0.4, 1.0],
          // ),
          image: DecorationImage(
            image: AssetImage('assets/img/background_pink_darken.png'),
            fit: BoxFit.cover,
          ),
        ),
        child: Scaffold(
            backgroundColor: Colors.transparent,
            body: Padding(
              padding: EdgeInsets.only(left: 60, right: 60),
              child: Column(
                children: [
                  Flexible(
                    flex: 1,
                    child: Container(),
                  ),
                  Flexible(
                    flex: 1,
                    child: Center(
                      child: Lottie.asset('assets/lottie/105550sparkle.json'),
                    ),
                  ),
                  Flexible(
                    flex: 2,
                    child: Column(
                      children: [
                        Flexible(
                            flex: 2,
                            child: Center(
                              child: Text(
                                '오늘의 기억 조각 완성 !',
                                style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 23,
                                    fontWeight: FontWeight.w500),
                              ),
                            )),
                        Flexible(
                          flex: 1,
                          child: Text('카드를 터치해보세요',
                              style:
                                  TextStyle(color: Colors.grey, fontSize: 16)),
                        )
                      ],
                    ),
                  ),
                  Flexible(
                      flex: 10,
                      child: GestureDetector(
                          onTap: () {
                            _toggleCardExpansion();
                          },
                          child: AnimatedOpacity(
                            opacity: opacityLevel,
                            duration: Duration(seconds: 3),
                            child: Center(
                              child: Transform(
                                transform:
                                    Matrix4.rotationY(_animation.value * 3.14),
                                alignment: Alignment.center,
                                child: _isFrontVisible
                                    ? _buildFront()
                                    : _buildBack(),
                              ),
                            ),
                          ))),
                  Flexible(
                      flex: 4,
                      child: Column(
                        children: [
                          Flexible(
                            flex: 1,
                            child: Container(),
                          ),
                          Flexible(
                            flex: 4,
                            child: Column(
                              children: [
                                Flexible(
                                  flex: 2,
                                  child: Center(
                                    child: _buttonList(context),
                                  ),
                                ),
                                Flexible(
                                  flex: 3,
                                  child: Row(
                                    children: [
                                      Flexible(
                                        flex: 2,
                                        child: Center(
                                          child: TextButton(
                                        onPressed: () {
                                          Navigator.push(
                                              context,
                                              MaterialPageRoute(
                                                builder: (context) =>
                                                    const CardCreate(),
                                              ));
                                        },
                                        child: Text(
                                          '카드 추가 생성하기 →',
                                          style: TextStyle(
                                              color: Colors.white,
                                              fontWeight: FontWeight.w800),
                                        ),
                                      )),),
                                      Flexible(flex: 1, child: Center(
                                        child: GestureDetector(
                                          onTap: (){
                                            CheckKakao();
                                          },
                                          child: Text(
                                            '공유 →',
                                            style: TextStyle(
                                                color: Colors.white,
                                                fontWeight: FontWeight.w800),
                                          ),
                                        ),
                                      ),)
                                    ],
                                  ),
                                )
                              ],
                            ),
                          ),
                          Flexible(flex: 1, child: Container())
                        ],
                      ))
                ],
              ),
            )),
      ),
    );
  }

  Widget _buildBack() {
    return Container(
      decoration: BoxDecoration(
        color: Colors.transparent,
        border: Border.all(
          width: 10,
          color: Color(0xffECE0CA),
        ),
        borderRadius: BorderRadius.circular(30),
      ),
      child: Transform(
        alignment: Alignment.center,
        transform: Matrix4.rotationY(math.pi),
        child: Container(
          decoration: BoxDecoration(
            borderRadius: BorderRadius.all(Radius.circular(20)),
            image: DecorationImage(
              fit: BoxFit.cover,
              image: NetworkImage(widget.card['cardImageUrl']),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildFront() {
    return Container(
        decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(30),
            image: DecorationImage(
                image: AssetImage('assets/img/cards.jpg'), fit: BoxFit.cover),
            border: Border.all(width: 10, color: Colors.grey)));
  }

  void _toggleCardExpansion() {
    if (_isFrontVisible) {
      _controller.forward().then((value) {
        setState(() {
          _isFrontVisible = false;
        });
      });
    } else {
      _controller.reverse().then((value) {
        setState(() {
          _isFrontVisible = true;
        });
      });
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}
