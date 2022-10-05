import http.client
from urllib import request as urllib_request
from flask import Flask, render_template, request
import json
import time
import datetime

app = Flask(__name__)

cook: bool = False


def find_student_class(name):
    http_client = None
    try:
        http_client = http.client.HTTPConnection('10.1.47.99:8000')
        http_client.request('GET', '/dataservice/api/1/163?full_name=' + urllib_request.pathname2url(name))
        response = http_client.getresponse()
        json_string = response.read().decode()
        json_object = json.loads(json_string)
        student_json_list = json_object['data']['rows']
        return student_json_list
    except Exception as e:
        return e
    finally:
        if http_client:
            http_client.close()


def find_student_picture(id):
    http_client = None
    try:
        http_client = http.client.HTTPConnection('10.1.47.99:8000')
        http_client.request('GET', '/dataservice/api/1/165?stu_id=' + urllib_request.pathname2url(id))
        response = http_client.getresponse()
        json_string = response.read().decode()
        json_object = json.loads(json_string)
        student_picture = ("http://data.ourschool.cn:180/dmp/" + json_object['data']['rows'][0]['photo']).strip(',')
        return student_picture
    except Exception as e:
        return e
    finally:
        if http_client:
            http_client.close()


@app.route('/')
def index():
    return 'hello world!'


@app.route('/pepper')
def pepper():
    global cook
    if not cook:
        cook = True
        print("接收到Pepper请求")
    return ""


@app.route('/raspberry')
def raspberry():
    global cook
    if cook:
        cook = False
        return "1"


@app.route('/FindStudentSubmit')
def find_student_submit():
    return render_template('find_student_submit.html')


@app.route('/FindStudentResult', methods=['POST'])
def find_student_result():
    student_name = request.form.get('student_name')
    if student_name != '':
        student_json_list = find_student_class(student_name)
        if len(student_json_list) != 0:
            student_id = student_json_list[0]['school_card_number']
            student_picture = find_student_picture(student_id)
            return render_template('find_student_result.html', list=student_json_list, picture=student_picture)
        else:
            return '未找到此人'
    else:
        return render_template('find_student_submit.html', msg='请输入姓名')


@app.route('/DataDisplay')
def data_display():
    return render_template('data_display.html')


def find_attendance(id_number):
    id_time = datetime.date.today().__str__()
    print(id_time)
    http_client = None
    try:
        http_client = http.client.HTTPConnection('10.1.47.99:8000')
        http_client.request('GET', '/dataservice/api/587/171?id_number=' + urllib_request.pathname2url(id_number) + '&time=' + urllib_request.pathname2url(id_time))
        response = http_client.getresponse()
        json_string = response.read().decode()
        json_object = json.loads(json_string)
        attendance_json_list = json_object['data']['rows']
        return attendance_json_list
    except Exception as e:
        return e
    finally:
        if http_client:
            http_client.close()


@app.route('/FindAttendanceSubmit')
def find_attendance_submit():
    return render_template('find_attendance_submit.html')


@app.route('/FindAttendanceResult', methods=['POST'])
def find_attendance_result():
    id_number = request.form.get('id_number')
    print(id_number)
    if id_number != '':
        attendance_json_list = find_attendance(id_number)
        if len(attendance_json_list) != 0:
            teacher_name = attendance_json_list[0]['name']
            return render_template('find_attendance_result.html', list=attendance_json_list)
        else:
            return '未找到此人'
    else:
        return render_template('find_attendance_submit.html', msg='请输入姓名')


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80)
