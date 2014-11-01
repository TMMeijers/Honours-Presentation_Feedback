import cv2
from cv2 import cv

import array
import numpy as np

import pykinect
from pykinect import nui

if __name__ == '__main__':
  window = 'Kinect RGB'
  cv.NamedWindow(window)  
  
  image = cv.CreateImage((640,480), cv.IPL_DEPTH_8U, 4)
  matrix = array.array('c')
  matrix.fromstring(image.tostring())
  address = matrix.buffer_info()[0]
  
  cur_image = None
  def video_frame_ready(frame):
    frame.image.copy_bits(address)

  def depth_frame_ready(frame):
    frame.im

  kinect = nui.Runtime()
  kinect.video_frame_ready += video_frame_ready
  kinect.depth_frame_ready += depth_frame_ready
  kinect.video_stream.open(nui.ImageStreamType.Video,
        2,
        nui.ImageResolution.Resolution640x480,
        nui.ImageType.Color)
  
  while cv.WaitKey(30) < 0:     
    cv.SetData(image, matrix.tostring())
    cv.ShowImage(window, image)


  print('done waiting')
  kinect.close()
  cv.DestroyAllWindows()