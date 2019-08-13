graph [

  directed 0

  node [
    id 1
    label "{HE1}    {4, 5}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 2
    label "{HE2}    {5, 6}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 3
    label "{HE0}    {1, 2, 3, 4}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  edge [
    source 1
    target 2
  ]

  edge [
    source 1
    target 3
  ]

]
