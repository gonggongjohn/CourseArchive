import pandas as pd
from models import Treasure


def load_treasure():
    tool_df = pd.read_csv('data/tool_list.csv')
    for index, row in tool_df.iterrows():
        tool_obj = Treasure(name=row.loc['名称'], type=1, gain=row.loc['加成'])
        tool_obj.save()
    accessory_df = pd.read_csv('data/accessory_list.csv')
    for index, row in accessory_df.iterrows():
        accessory_obj = Treasure(name=row.loc['名称'], type=2, gain=row.loc['加成'])
        accessory_obj.save()
