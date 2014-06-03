<html style='width:100%; height:100%; border:0; margin:0; padding:0;'>
  <head>
    <meta http-equiv='X-UA-Compatible' content='chrome=1'></meta>
    <meta http-equiv='Content-Type' content='text/html;charset=utf-8'></meta>
    <r:require module="items3d"/>

    <style>
      .x3dom-logContainer {
        bottom: 0px;
        position: absolute;
      }

      body {
        font-family: 'Asap', Helvetica, Arial, sans-serif;
        background: #FFFFFF;
        /* IE10 Consumer Preview */
        background-image: -ms-linear-gradient(top, #F0F0F0 0%, #616161 100%);
        /* Mozilla Firefox */
        background-image: -moz-linear-gradient(top, #F0F0F0 0%, #616161 100%);
        /* Opera */
        background-image: -o-linear-gradient(top, #F0F0F0 0%, #616161 100%);
        /* Webkit (Safari/Chrome 10) */
        background-image: -webkit-gradient(linear, left top, left bottom, color-stop(0, #F0F0F0),
          color-stop(1, #616161));
        /* Webkit (Chrome 11+) */
        background-image: -webkit-linear-gradient(top, #F0F0F0 0%, #616161 100%);
        /* W3C Markup, IE10 Release Preview */
        background-image: linear-gradient(to bottom, #F0F0F0 0%, #616161 100%);
      }
    </style>
</head>
  <body style='width:100%; height:100%; border:0; margin:0; padding:0;'>
    <div id='HUDs_Div'> 
      <div id='Interaction_Toolbox' style='margin:2px; padding:4px; padding-right:150px; background-color:rgba(199,202,204,.7);position:absolute; z-index:1000; right:0px; top:0px;'> 
        <table> 
          <tr> 
            <td>Navigation Mode: 
            </td> 
            <td align='right'> 
              <select style='float:right;' onchange='if (this.selectedIndex !== undefined) { var e = document.getElementById(&apos;x3dElement&apos;); if (this.options[this.selectedIndex].value === &apos;examine&apos;) { e.runtime.examine(); } else if (this.options[this.selectedIndex].value === &apos;lookat&apos;) { e.runtime.lookAt(); } else if (this.options[this.selectedIndex].value === &apos;walk&apos;) { e.runtime.walk(); } else if (this.options[this.selectedIndex].value === &apos;fly&apos;) { e.runtime.fly(); } else if (this.options[this.selectedIndex].value === &apos;helicopter&apos;) { e.runtime.helicopter(); } else if (this.options[this.selectedIndex].value === &apos;none&apos;) { e.runtime.noNav(); } }'> 
                <option value='examine'>Examine 
                </option> 
                <option value='lookat'>LookAt 
                </option> 
                <option value='walk'>Walk 
                </option> 
                <option value='fly'>Fly 
                </option> 
                <option value='helicopter'>Helicopter 
                </option> 
                <option value='none'>None 
                </option> 
              </select> 
            </td> 
          </tr> 
          <tr> 
            <td>Debug Display: 
            </td> 
            <td align='right'> 
              <input type='checkbox' onclick='if (this.checked) { document.getElementById(&apos;x3dElement&apos;).runtime.statistics(true); } else { document.getElementById(&apos;x3dElement&apos;).runtime.statistics(false); }'> 
              </input> Stats 
              <input type='checkbox' onclick='document.getElementById(&apos;x3dElement&apos;).runtime.debug();'> 
              </input> Log 
            </td> 
          </tr> 
          <tr> 
            <td> 
              <button onclick='document.getElementById(&apos;x3dElement&apos;).runtime.showAll();'> Show Everything 
              </button> 
            </td> 
            <td> 
              <button onclick='document.getElementById(&apos;x3dElement&apos;).runtime.resetView();'> Reset View 
              </button> 
            </td> 
          </tr> 
        </table> 
      </div> 
    </div>
    <x3d id='x3dElement' showStat='false' showLog='false' style='width:100%; height:100%; border:0; margin:0; padding:0;'>
      <scene DEF='scene'>
        <viewpoint DEF='AOPT_CAM' centerOfRotation='-7.49828 246.898 -33.5825' position='-7.49828 246.898 717.793'></viewpoint>
        <shape DEF='_G_0'>
          <appearance DEF='AOPT_Appearance_168934000'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex007.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_13' vertexCount='166166 6138' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-42.7577781677 378.664733887 -21.0838012695' size='122.369529724 214.867401123 203.436141968' index='binGeo/BG_13_indexBinary.bin' coord='binGeo/BG_13_coordBinary.bin' normal='binGeo/BG_13_normalBinary.bin' texCoord='binGeo/BG_13_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_1'>
          <appearance USE='AOPT_Appearance_168934000'></appearance>
          <binaryGeometry DEF='BG_34' vertexCount='60428 4089' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-44.3482818604 362.595062256 -24.4380187988' size='118.658370972 249.681152344 210.972290039' index='binGeo/BG_34_indexBinary.bin' coord='binGeo/BG_34_coordBinary.bin' normal='binGeo/BG_34_normalBinary.bin' texCoord='binGeo/BG_34_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_2'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex008.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_3' vertexCount='11005 1185' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-19.6411514282 387.217681885 17.8983078003' size='144.053070068 216.254760742 174.718521118' index='binGeo/BG_3_indexBinary.bin' coord='binGeo/BG_3_coordBinary.bin' normal='binGeo/BG_3_normalBinary.bin' texCoord='binGeo/BG_3_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_3'>
          <appearance DEF='AOPT_Appearance_165619216'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex009.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_1' vertexCount='165745 5775' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-2.96635437012 342.875305176 20.3328323364' size='144.057281494 211.842681885 193.945007324' index='binGeo/BG_1_indexBinary.bin' coord='binGeo/BG_1_coordBinary.bin' normal='binGeo/BG_1_normalBinary.bin' texCoord='binGeo/BG_1_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_4'>
          <appearance USE='AOPT_Appearance_165619216'></appearance>
          <binaryGeometry DEF='BG_2' vertexCount='10312 1302' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-4.75100708008 356.398345947 14.2947692871' size='147.20123291 238.159545898 184.657989502' index='binGeo/BG_2_indexBinary.bin' coord='binGeo/BG_2_coordBinary.bin' normal='binGeo/BG_2_normalBinary.bin' texCoord='binGeo/BG_2_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_5'>
          <appearance DEF='AOPT_Appearance_169013408'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex010.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_31' vertexCount='167804 5940' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-7.35306549072 183.631835938 12.4212188721' size='186.800384521 370.870941162 207.136230469' index='binGeo/BG_31_indexBinary.bin' coord='binGeo/BG_31_coordBinary.bin' normal='binGeo/BG_31_normalBinary.bin' texCoord='binGeo/BG_31_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_6'>
          <appearance USE='AOPT_Appearance_169013408'></appearance>
          <binaryGeometry DEF='BG_14' vertexCount='75633 4569' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-7.75173187256 183.205337524 10.8790130615' size='191.0287323 370.04800415 203.649765015' index='binGeo/BG_14_indexBinary.bin' coord='binGeo/BG_14_coordBinary.bin' normal='binGeo/BG_14_normalBinary.bin' texCoord='binGeo/BG_14_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_7'>
          <appearance DEF='AOPT_Appearance_185815152'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex011.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_35' vertexCount='168901 4929' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-75.4917831421 346.621154785 -21.5842514038' size='54.8769416809 206.352874756 195.309539795' index='binGeo/BG_35_indexBinary.bin' coord='binGeo/BG_35_coordBinary.bin' normal='binGeo/BG_35_normalBinary.bin' texCoord='binGeo/BG_35_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_8'>
          <appearance USE='AOPT_Appearance_185815152'></appearance>
          <binaryGeometry DEF='BG_4' vertexCount='163803 5712' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-54.3997535706 368.20300293 -25.0130310059' size='93.823387146 246.8462677 195.577682495' index='binGeo/BG_4_indexBinary.bin' coord='binGeo/BG_4_coordBinary.bin' normal='binGeo/BG_4_normalBinary.bin' texCoord='binGeo/BG_4_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_9'>
          <appearance USE='AOPT_Appearance_185815152'></appearance>
          <binaryGeometry DEF='BG_33' vertexCount='111724 6075' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-53.3159866333 365.793121338 -41.9362182617' size='101.026885986 241.531326294 235.80267334' index='binGeo/BG_33_indexBinary.bin' coord='binGeo/BG_33_coordBinary.bin' normal='binGeo/BG_33_normalBinary.bin' texCoord='binGeo/BG_33_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_10'>
          <appearance DEF='AOPT_Appearance_168382864'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex012.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_0' vertexCount='170017 5790' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-4.48793792725 419.112182617 -120.453010559' size='174.529205322 78.6549072266 128.02796936' index='binGeo/BG_0_indexBinary.bin' coord='binGeo/BG_0_coordBinary.bin' normal='binGeo/BG_0_normalBinary.bin' texCoord='binGeo/BG_0_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_11'>
          <appearance USE='AOPT_Appearance_168382864'></appearance>
          <binaryGeometry DEF='BG_37' vertexCount='15235 1236' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-4.89824676514 424.565643311 -110.810928345' size='174.642593384 86.2776489258 141.693939209' index='binGeo/BG_37_indexBinary.bin' coord='binGeo/BG_37_coordBinary.bin' normal='binGeo/BG_37_normalBinary.bin' texCoord='binGeo/BG_37_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_12'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex013.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_36' vertexCount='74458 5253' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-58.9898300171 375.051147461 -48.3203125' size='89.9054260254 217.249664307 249.22833252' index='binGeo/BG_36_indexBinary.bin' coord='binGeo/BG_36_coordBinary.bin' normal='binGeo/BG_36_normalBinary.bin' texCoord='binGeo/BG_36_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_13'>
          <appearance DEF='AOPT_Appearance_308979936'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex014.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_12' vertexCount='173541 4629' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-6.11466217041 27.7249221802 -0.541679382324' size='184.478012085 58.940032959 136.365631104' index='binGeo/BG_12_indexBinary.bin' coord='binGeo/BG_12_coordBinary.bin' normal='binGeo/BG_12_normalBinary.bin' texCoord='binGeo/BG_12_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_14'>
          <appearance USE='AOPT_Appearance_308979936'></appearance>
          <binaryGeometry DEF='BG_32' vertexCount='112432 4455' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-7.57238006592 49.0047416687 2.14115142822' size='190.861953735 101.672935486 146.064422607' index='binGeo/BG_32_indexBinary.bin' coord='binGeo/BG_32_coordBinary.bin' normal='binGeo/BG_32_normalBinary.bin' texCoord='binGeo/BG_32_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_15'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex015.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_15' vertexCount='6141 381' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-23.2064247131 377.526824951 33.4443817139' size='102.829185486 235.090026855 110.615913391' index='binGeo/BG_15_indexBinary.bin' coord='binGeo/BG_15_coordBinary.bin' normal='binGeo/BG_15_normalBinary.bin' texCoord='binGeo/BG_15_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_16'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex016.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_16' vertexCount='165647 4998' primType='"TRIANGLESTRIP" "TRIANGLES"' position='1.14324951172 121.286621094 1.05266571045' size='176.589309692 244.936706543 143.993011475' index='binGeo/BG_16_indexBinary.bin' coord='binGeo/BG_16_coordBinary.bin' normal='binGeo/BG_16_normalBinary.bin' texCoord='binGeo/BG_16_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_17'>
          <appearance DEF='AOPT_Appearance_168031472'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex017.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_17' vertexCount='171707 5733' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-78.9995422363 117.507209778 0.667068481445' size='51.608089447 235.644058228 138.018341064' index='binGeo/BG_17_indexBinary.bin' coord='binGeo/BG_17_coordBinary.bin' normal='binGeo/BG_17_normalBinary.bin' texCoord='binGeo/BG_17_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_18'>
          <appearance USE='AOPT_Appearance_168031472'></appearance>
          <binaryGeometry DEF='BG_18' vertexCount='1805 558' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-15.8195724487 122.77684021 15.2867240906' size='176.878570557 246.152709961 123.956604004' index='binGeo/BG_18_indexBinary.bin' coord='binGeo/BG_18_coordBinary.bin' normal='binGeo/BG_18_normalBinary.bin' texCoord='binGeo/BG_18_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_19'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex018.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_19' vertexCount='78392 5820' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-7.9631729126 299.313018799 -67.7714691162' size='154.30557251 169.321426392 210.122772217' index='binGeo/BG_19_indexBinary.bin' coord='binGeo/BG_19_coordBinary.bin' normal='binGeo/BG_19_normalBinary.bin' texCoord='binGeo/BG_19_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_20'>
          <appearance DEF='AOPT_Appearance_168034704'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex019.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_20' vertexCount='171086 5229' primType='"TRIANGLESTRIP" "TRIANGLES"' position='1.02392959595 365.139556885 37.9651679993' size='111.355583191 254.806869507 102.9637146' index='binGeo/BG_20_indexBinary.bin' coord='binGeo/BG_20_coordBinary.bin' normal='binGeo/BG_20_normalBinary.bin' texCoord='binGeo/BG_20_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_21'>
          <appearance USE='AOPT_Appearance_168034704'></appearance>
          <binaryGeometry DEF='BG_21' vertexCount='135904 5592' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-3.06762695313 366.659545898 4.97304534912' size='145.519744873 256.312530518 167.112045288' index='binGeo/BG_21_indexBinary.bin' coord='binGeo/BG_21_coordBinary.bin' normal='binGeo/BG_21_normalBinary.bin' texCoord='binGeo/BG_21_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_22'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex020.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_22' vertexCount='430 78' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-36.4732055664 381.986328125 -10.6487503052' size='116.182922363 204.520782471 182.921737671' index='binGeo/BG_22_indexBinary.bin' coord='binGeo/BG_22_coordBinary.bin' normal='binGeo/BG_22_normalBinary.bin' texCoord='binGeo/BG_22_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_23'>
          <appearance DEF='AOPT_Appearance_168037936'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex021.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_23' vertexCount='170906 6075' primType='"TRIANGLESTRIP" "TRIANGLES"' position='28.2014427185 201.035858154 -14.4678726196' size='123.211181641 237.571578979 195.10585022' index='binGeo/BG_23_indexBinary.bin' coord='binGeo/BG_23_coordBinary.bin' normal='binGeo/BG_23_normalBinary.bin' texCoord='binGeo/BG_23_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_24'>
          <appearance USE='AOPT_Appearance_168037936'></appearance>
          <binaryGeometry DEF='BG_24' vertexCount='43362 2940' primType='"TRIANGLESTRIP" "TRIANGLES"' position='4.74518585205 204.69178772 -2.72760772705' size='169.486801147 264.430603027 230.247711182' index='binGeo/BG_24_indexBinary.bin' coord='binGeo/BG_24_coordBinary.bin' normal='binGeo/BG_24_normalBinary.bin' texCoord='binGeo/BG_24_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_25'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex022.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_25' vertexCount='2782 423' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-30.5358963013 366.979156494 -75.000869751' size='143.322021484 200.526672363 192.705505371' index='binGeo/BG_25_indexBinary.bin' coord='binGeo/BG_25_coordBinary.bin' normal='binGeo/BG_25_normalBinary.bin' texCoord='binGeo/BG_25_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_26'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex023.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_26' vertexCount='480 57' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-4.65637588501 349.012695313 70.77734375' size='64.5880584717 139.959381104 73.8689880371' index='binGeo/BG_26_indexBinary.bin' coord='binGeo/BG_26_coordBinary.bin' normal='binGeo/BG_26_normalBinary.bin' texCoord='binGeo/BG_26_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_27'>
          <appearance DEF='AOPT_Appearance_168042784'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex024.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_27' vertexCount='174171 4599' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-16.4632034302 138.866088867 37.9700698853' size='176.18762207 197.106872559 71.3820343018' index='binGeo/BG_27_indexBinary.bin' coord='binGeo/BG_27_coordBinary.bin' normal='binGeo/BG_27_normalBinary.bin' texCoord='binGeo/BG_27_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_28'>
          <appearance USE='AOPT_Appearance_168042784'></appearance>
          <binaryGeometry DEF='BG_28' vertexCount='172957 4506' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-1.77332305908 68.8797912598 41.3981819153' size='180.110656738 133.191375732 72.2106399536' index='binGeo/BG_28_indexBinary.bin' coord='binGeo/BG_28_coordBinary.bin' normal='binGeo/BG_28_normalBinary.bin' texCoord='binGeo/BG_28_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_29'>
          <appearance USE='AOPT_Appearance_168042784'></appearance>
          <binaryGeometry DEF='BG_29' vertexCount='171243 4875' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-7.48029327393 102.836509705 39.1976623535' size='193.052444458 186.706710815 75.9764404297' index='binGeo/BG_29_indexBinary.bin' coord='binGeo/BG_29_coordBinary.bin' normal='binGeo/BG_29_normalBinary.bin' texCoord='binGeo/BG_29_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_30'>
          <appearance USE='AOPT_Appearance_168042784'></appearance>
          <binaryGeometry DEF='BG_30' vertexCount='165652 5475' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-7.67361450195 118.848831177 50.0812530518' size='193.640167236 241.200714111 126.040893555' index='binGeo/BG_30_indexBinary.bin' coord='binGeo/BG_30_coordBinary.bin' normal='binGeo/BG_30_normalBinary.bin' texCoord='binGeo/BG_30_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_31'>
          <appearance USE='AOPT_Appearance_168042784'></appearance>
          <binaryGeometry DEF='BG_38' vertexCount='88981 3846' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-7.84299468994 119.845649719 45.9382400513' size='193.357727051 243.151535034 122.707115173' index='binGeo/BG_38_indexBinary.bin' coord='binGeo/BG_38_coordBinary.bin' normal='binGeo/BG_38_normalBinary.bin' texCoord='binGeo/BG_38_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_32'>
          <appearance DEF='AOPT_Appearance_168044400'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex025.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_39' vertexCount='169618 5562' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-4.44525909424 391.220977783 -14.0079421997' size='174.779769897 203.223449707 221.704620361' index='binGeo/BG_39_indexBinary.bin' coord='binGeo/BG_39_coordBinary.bin' normal='binGeo/BG_39_normalBinary.bin' texCoord='binGeo/BG_39_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_33'>
          <appearance USE='AOPT_Appearance_168044400'></appearance>
          <binaryGeometry DEF='BG_40' vertexCount='143691 6795' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-6.05937194824 380.984741211 -25.5223846436' size='177.674407959 229.287506104 255.977722168' index='binGeo/BG_40_indexBinary.bin' coord='binGeo/BG_40_coordBinary.bin' normal='binGeo/BG_40_normalBinary.bin' texCoord='binGeo/BG_40_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_34'>
          <appearance DEF='AOPT_Appearance_168046016'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex026.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_41' vertexCount='172187 4755' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-9.97915649414 115.247261047 -36.123500824' size='178.333312988 198.10256958 70.5473403931' index='binGeo/BG_41_indexBinary.bin' coord='binGeo/BG_41_coordBinary.bin' normal='binGeo/BG_41_normalBinary.bin' texCoord='binGeo/BG_41_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_35'>
          <appearance USE='AOPT_Appearance_168046016'></appearance>
          <binaryGeometry DEF='BG_42' vertexCount='171618 4614' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-4.65673828125 126.307327271 -44.1852912903' size='187.145874023 248.769378662 53.7966079712' index='binGeo/BG_42_indexBinary.bin' coord='binGeo/BG_42_coordBinary.bin' normal='binGeo/BG_42_normalBinary.bin' texCoord='binGeo/BG_42_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_36'>
          <appearance USE='AOPT_Appearance_168046016'></appearance>
          <binaryGeometry DEF='BG_43' vertexCount='169182 4839' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-6.72359466553 121.562309265 -37.4936294556' size='190.629516602 238.914611816 67.2404632568' index='binGeo/BG_43_indexBinary.bin' coord='binGeo/BG_43_coordBinary.bin' normal='binGeo/BG_43_normalBinary.bin' texCoord='binGeo/BG_43_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_37'>
          <appearance USE='AOPT_Appearance_168046016'></appearance>
          <binaryGeometry DEF='BG_44' vertexCount='168802 4992' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-6.60565948486 118.346595764 -36.7804260254' size='189.276672363 232.850372314 68.986541748' index='binGeo/BG_44_indexBinary.bin' coord='binGeo/BG_44_coordBinary.bin' normal='binGeo/BG_44_normalBinary.bin' texCoord='binGeo/BG_44_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_38'>
          <appearance USE='AOPT_Appearance_168046016'></appearance>
          <binaryGeometry DEF='BG_45' vertexCount='141313 6297' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-7.25244140625 128.225418091 -17.5209960938' size='193.34487915 252.90838623 107.467910767' index='binGeo/BG_45_indexBinary.bin' coord='binGeo/BG_45_coordBinary.bin' normal='binGeo/BG_45_normalBinary.bin' texCoord='binGeo/BG_45_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_39'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex027.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_46' vertexCount='131896 5232' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-20.3266906738 232.239578247 2.17375946045' size='146.723602295 276.291320801 227.010986328' index='binGeo/BG_46_indexBinary.bin' coord='binGeo/BG_46_coordBinary.bin' normal='binGeo/BG_46_normalBinary.bin' texCoord='binGeo/BG_46_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_40'>
          <appearance DEF='AOPT_Appearance_168049248'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex028.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_47' vertexCount='170217 5697' primType='"TRIANGLESTRIP" "TRIANGLES"' position='53.2797851563 345.840484619 -53.9305419922' size='59.3833808899 194.383773804 204.784759521' index='binGeo/BG_47_indexBinary.bin' coord='binGeo/BG_47_coordBinary.bin' normal='binGeo/BG_47_normalBinary.bin' texCoord='binGeo/BG_47_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_41'>
          <appearance USE='AOPT_Appearance_168049248'></appearance>
          <binaryGeometry DEF='BG_48' vertexCount='164982 6294' primType='"TRIANGLESTRIP" "TRIANGLES"' position='25.2388343811 340.27130127 -52.6125030518' size='114.438430786 184.390975952 204.547302246' index='binGeo/BG_48_indexBinary.bin' coord='binGeo/BG_48_coordBinary.bin' normal='binGeo/BG_48_normalBinary.bin' texCoord='binGeo/BG_48_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_42'>
          <appearance USE='AOPT_Appearance_168049248'></appearance>
          <binaryGeometry DEF='BG_49' vertexCount='21085 2796' primType='"TRIANGLESTRIP" "TRIANGLES"' position='23.6060600281 352.158599854 -50.4640274048' size='118.646331787 206.727706909 237.848007202' index='binGeo/BG_49_indexBinary.bin' coord='binGeo/BG_49_coordBinary.bin' normal='binGeo/BG_49_normalBinary.bin' texCoord='binGeo/BG_49_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_43'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
          </appearance>
          <binaryGeometry DEF='BG_50' vertexCount='27 3' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-94.0403289795 119.010253906 -11.48097229' size='0.228294372559 0.026008605957 0.142468452454' index='binGeo/BG_50_indexBinary.bin' coord='binGeo/BG_50_coordBinary.bin' normal='binGeo/BG_50_normalBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_44'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex000.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_51' vertexCount='588 126' primType='"TRIANGLESTRIP" "TRIANGLES"' position='11.8074760437 263.120483398 3.932472229' size='112.50504303 300.850128174 216.919799805' index='binGeo/BG_51_indexBinary.bin' coord='binGeo/BG_51_coordBinary.bin' normal='binGeo/BG_51_normalBinary.bin' texCoord='binGeo/BG_51_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_45'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex001.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_52' vertexCount='161 66' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-31.635017395 354.465759277 -90.4199523926' size='144.61505127 165.807250977 134.371520996' index='binGeo/BG_52_indexBinary.bin' coord='binGeo/BG_52_coordBinary.bin' normal='binGeo/BG_52_normalBinary.bin' texCoord='binGeo/BG_52_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_46'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex002.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_53' vertexCount='853 96' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-9.81288909912 254.015197754 9.61032867432' size='172.865463257 270.135955811 175.405456543' index='binGeo/BG_53_indexBinary.bin' coord='binGeo/BG_53_coordBinary.bin' normal='binGeo/BG_53_normalBinary.bin' texCoord='binGeo/BG_53_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_47'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex003.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_5' vertexCount='105976 7176' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-5.84957885742 343.213012695 -80.5213165283' size='177.618041992 280.74887085 207.867156982' index='binGeo/BG_5_indexBinary.bin' coord='binGeo/BG_5_coordBinary.bin' normal='binGeo/BG_5_normalBinary.bin' texCoord='binGeo/BG_5_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_48'>
          <appearance>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex004.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_6' vertexCount='127 123' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-6.21739196777 235.801239014 -47.7897033691' size='176.400741577 244.220031738 129.619506836' index='binGeo/BG_6_indexBinary.bin' coord='binGeo/BG_6_coordBinary.bin' normal='binGeo/BG_6_normalBinary.bin' texCoord='binGeo/BG_6_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_49'>
          <appearance DEF='AOPT_Appearance_168914192'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex005.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_7' vertexCount='173175 5910' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-20.0386886597 313.765136719 -74.6357345581' size='120.170013428 156.108261108 219.307907104' index='binGeo/BG_7_indexBinary.bin' coord='binGeo/BG_7_coordBinary.bin' normal='binGeo/BG_7_normalBinary.bin' texCoord='binGeo/BG_7_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_50'>
          <appearance USE='AOPT_Appearance_168914192'></appearance>
          <binaryGeometry DEF='BG_8' vertexCount='169306 5925' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-32.4948272705 332.327209473 -78.4316711426' size='138.287078857 184.975738525 210.297180176' index='binGeo/BG_8_indexBinary.bin' coord='binGeo/BG_8_coordBinary.bin' normal='binGeo/BG_8_normalBinary.bin' texCoord='binGeo/BG_8_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_51'>
          <appearance USE='AOPT_Appearance_168914192'></appearance>
          <binaryGeometry DEF='BG_9' vertexCount='125944 7251' primType='"TRIANGLESTRIP" "TRIANGLES"' position='-22.0258560181 363.155273438 -71.5093841553' size='161.139068604 256.234985352 225.922058105' index='binGeo/BG_9_indexBinary.bin' coord='binGeo/BG_9_coordBinary.bin' normal='binGeo/BG_9_normalBinary.bin' texCoord='binGeo/BG_9_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_52'>
          <appearance DEF='AOPT_Appearance_168923440'>
            <material diffuseColor='0.7 0.7 0.5' emissiveColor='0.05 0.05 0.05' shininess='0.15625' specularColor='0.1 0.1 0.1'></material>
            <imageTexture repeatS='false' repeatT='false' url='"Nofretete_tex006.jpg"'></imageTexture>
          </appearance>
          <binaryGeometry DEF='BG_10' vertexCount='168911 5697' primType='"TRIANGLESTRIP" "TRIANGLES"' position='21.3374328613 380.261047363 -4.49338531494' size='123.178329468 223.881072998 163.415710449' index='binGeo/BG_10_indexBinary.bin' coord='binGeo/BG_10_coordBinary.bin' normal='binGeo/BG_10_normalBinary.bin' texCoord='binGeo/BG_10_texCoordBinary.bin'></binaryGeometry>
        </shape>
        <shape DEF='_G_53'>
          <appearance USE='AOPT_Appearance_168923440'></appearance>
          <binaryGeometry DEF='BG_11' vertexCount='60108 3936' primType='"TRIANGLESTRIP" "TRIANGLES"' position='21.4419250488 368.09576416 -20.5695343018' size='123.05909729 253.38822937 197.833557129' index='binGeo/BG_11_indexBinary.bin' coord='binGeo/BG_11_coordBinary.bin' normal='binGeo/BG_11_normalBinary.bin' texCoord='binGeo/BG_11_texCoordBinary.bin'></binaryGeometry>
        </shape>
      </scene>
    </x3d>
  </body>
</html>