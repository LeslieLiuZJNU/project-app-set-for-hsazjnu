import time
import datetime
from urllib import request as urllib_request

if __name__ == '__main__':
    print(urllib_request.pathname2url('2021-04-08'))
    time_now = 1617857075
    print(time_now)
    dt_now = datetime.datetime.fromtimestamp(time_now)
    time_str = dt_now.strftime('%Y/%#m/%d')
    print(time_str)

    # 今天日期
    today = datetime.date.today()
    # 昨天时间
    yesterday = today - datetime.timedelta(days=1)
    # 明天时间
    tomorrow = today + datetime.timedelta(days=1)
    acquire = today + datetime.timedelta(days=2)
    # 昨天开始时间戳
    yesterday_start_time = int(time.mktime(time.strptime(str(yesterday), '%Y-%m-%d')))
    # 昨天结束时间戳
    yesterday_end_time = int(time.mktime(time.strptime(str(today), '%Y-%m-%d'))) - 1
    # 今天开始时间戳
    today_start_time = yesterday_end_time + 1
    # 今天结束时间戳
    today_end_time = int(time.mktime(time.strptime(str(tomorrow), '%Y-%m-%d'))) - 1
    # 明天开始时间戳
    tomorrow_start_time = int(time.mktime(time.strptime(str(tomorrow), '%Y-%m-%d')))
    # 明天结束时间戳
    tomorrow_end_time = int(time.mktime(time.strptime(str(acquire), '%Y-%m-%d'))) - 1

    print(today_start_time)