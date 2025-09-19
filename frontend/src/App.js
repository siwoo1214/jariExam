import React, { useState, useEffect } from 'react';
import { parkingAPI } from './services/api';
import './App.css';

// 카카오맵 동적 로딩 훅

const useKakaoMap = () => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadKakaoMap = () => {
    // 이미 로드된 경우
    if (window.kakao && window.kakao.maps) {
      setIsLoaded(true);
      return Promise.resolve();
    }

    // 이미 로딩 중인 경우
    if (isLoading) {
      return Promise.reject(new Error('이미 로딩 중입니다'));
    }

    setIsLoading(true);
    setError(null);

    return new Promise((resolve, reject) => {
      // 기존 스크립트 제거
      const existingScript = document.querySelector('script[src*="dapi.kakao.com"]');
      if (existingScript) {
        existingScript.remove();
      }

      const script = document.createElement('script');
      script.type = 'text/javascript';
      script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=11a8234bf504a62d4570a8d247e94a52&autoload=false';
      
      script.onload = () => {
        // autoload=false로 설정했으므로 수동으로 로드
        if (window.kakao && window.kakao.maps) {
          window.kakao.maps.load(() => {
            setIsLoaded(true);
            setIsLoading(false);
            console.log('카카오맵 동적 로드 성공');
            resolve();
          });
        } else {
          const err = new Error('카카오맵 스크립트 로드 후 객체를 찾을 수 없습니다');
          setError(err);
          setIsLoading(false);
          reject(err);
        }
      };
      
      script.onerror = () => {
        const err = new Error('카카오맵 스크립트 로드 실패');
        setError(err);
        setIsLoading(false);
        reject(err);
      };

      document.head.appendChild(script);
      
      // 10초 타임아웃
      setTimeout(() => {
        if (isLoading) {
          const err = new Error('카카오맵 로드 타임아웃');
          setError(err);
          setIsLoading(false);
          reject(err);
        }
      }, 10000);
    });
  };

  return { isLoaded, isLoading, error, loadKakaoMap };
};

function App() {
  const [parkingData, setParkingData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [map, setMap] = useState(null);
  const [markers, setMarkers] = useState([]);
  
  const { isLoaded: isMapLoaded, isLoading: isMapLoading, error: mapError, loadKakaoMap } = useKakaoMap();

  // API에서 주차장 데이터 가져오기
  useEffect(() => {
    const fetchParkingData = async () => {
      console.log('데이터 로딩 시작...');
      try {
        const response = await parkingAPI.getParkingList();
        // console.log('전체 API 응답:', response);
        // console.log('응답 데이터:', response.data);
        // console.log('데이터 타입:', typeof response.data);
        // console.log('데이터 길이:', response.data ? response.data.length : 'undefined');
        
        setParkingData(response.data);
        setLoading(false);
      } catch (error) {
        console.error('주차장 데이터를 불러오는데 실패했습니다:', error);
        
        // 테스트 API로 대체 시도
        try {
          console.log('테스트 API 시도 중...');
          const testResponse = await fetch('http://localhost:8080/test/parking');
          const testData = await testResponse.json();
          console.log('테스트 API 성공:', testData);
          setParkingData([testData]);
        } catch (testError) {
          console.error('테스트 API도 실패:', testError);
        }
        
        setLoading(false);
      }
    };

    fetchParkingData();
  }, []);

  // 카카오맵 로드 및 초기화
  useEffect(() => {
    loadKakaoMap().catch(error => {
      console.error('카카오맵 로드 실패:', error);
    });
  }, []);

  // 맵 초기화 (카카오맵 로드 완료 후)
  useEffect(() => {
    if (!isMapLoaded) return;
    
    let isMounted = true;
    
    const initializeMap = () => {
      console.log('카카오맵 초기화 시도 중...');
      
      try {
        const mapContainer = document.getElementById('map');
        if (!mapContainer) {
          console.error('지도 컨테이너를 찾을 수 없습니다.');
          return;
        }
        
        // 컨테이너 크기 강제 설정
        mapContainer.style.width = '100%';
        mapContainer.style.height = '100%';
        mapContainer.style.minHeight = '400px';
        
        console.log('컨테이너 크기:', mapContainer.offsetWidth, 'x', mapContainer.offsetHeight);
        
        const mapOption = {
          center: new window.kakao.maps.LatLng(37.556460, 126.936437),
          level: 3
        };

        const mapInstance = new window.kakao.maps.Map(mapContainer, mapOption);
        
        // 지도 생성 후 간단한 테스트 마커 추가
        setTimeout(() => {
          if (mapInstance && window.kakao && window.kakao.maps) {
            // 테스트 마커 추가
            const testMarker = new window.kakao.maps.Marker({
              position: new window.kakao.maps.LatLng(37.556460, 126.936437),
              map: mapInstance
            });
            
            console.log('테스트 마커 추가 완료');
          }
        }, 200);
        
        if (isMounted) {
          setMap(mapInstance);
          
          try {
            mapInstance.addOverlayMapTypeId(window.kakao.maps.MapTypeId.TRAFFIC);
          } catch (trafficError) {
            console.warn('교통정보 로드 실패:', trafficError);
          }
          
          console.log('카카오맵 초기화 완료');
        }
        
      } catch (error) {
        console.error('카카오맵 초기화 실패:', error);
      }
    };
    
    // DOM 렌더링 대기 후 시작
    const timeoutId = setTimeout(initializeMap, 300);
    
    return () => {
      isMounted = false;
      clearTimeout(timeoutId);
    };
  }, [isMapLoaded]);

  // 마커 생성 및 업데이트
  useEffect(() => {
    if (map && parkingData.length > 0 && window.kakao && window.kakao.maps) {
      try {
        if (markers && markers.length > 0) {
          markers.forEach(marker => {
            try {
              marker.setMap(null);
            } catch (e) {
              console.warn('마커 제거 실패:', e);
            }
          });
        }
        
        const originalPositions = [
          {
            name: '창천공영주차장',
            latlng: { lat: 37.554974, lng: 126.939335 }
          },
          {
            name: '신촌 노외주차장',
            latlng: { lat: 37.554525, lng: 126.933814 }
          },
          {
            name: '신촌역동측광장공영주차장',
            latlng: { lat: 37.559170, lng: 126.934684 }
          },
          {
            name: '마포공영주차장',
            latlng: { lat: 37.554512, lng: 126.935439 }
          }
        ];
        
        const newMarkers = parkingData.slice(0, 10).map((parking, index) => {
          try {
            let position;
            if (index < originalPositions.length) {
              const original = originalPositions[index];
              position = { lat: original.latlng.lat, lng: original.latlng.lng };
            } else {
              const baseLat = 37.555 + (Math.random() - 0.5) * 0.01;
              const baseLng = 126.935 + (Math.random() - 0.5) * 0.01;
              position = { lat: baseLat, lng: baseLng };
            }
            
            const marker = new window.kakao.maps.Marker({
              map: map,
              position: new window.kakao.maps.LatLng(position.lat, position.lng)
            });

            const infowindow = new window.kakao.maps.InfoWindow({
              content: `
                <div style="padding:8px; min-width:180px; max-width:250px;">
                  <div style="font-weight:bold; color:#333; margin-bottom:4px; font-size:13px;">
                    ${parking.PKLT_NM || '주차장'}
                  </div>
                  <div style="font-size:11px; color:#666; margin-bottom:4px;">
                    ${parking.ADDR || '주소 정보 없음'}
                  </div>
                  <div style="font-size:11px;">
                    <span style="color:#007bff;">총 ${parking.TPKCT || 0}대</span> | 
                    <span style="color:#28a745;">현재 ${parking.NOW_PRK_VHCL_CNT || 0}대</span>
                  </div>
                </div>
              `
            });

            window.kakao.maps.event.addListener(marker, 'mouseover', () => {
              infowindow.open(map, marker);
            });

            window.kakao.maps.event.addListener(marker, 'mouseout', () => {
              infowindow.close();
            });

            return marker;
          } catch (markerError) {
            console.warn('마커 생성 실패:', markerError);
            return null;
          }
        }).filter(marker => marker !== null);

        setMarkers(newMarkers);
        console.log(`마커 ${newMarkers.length}개 생성 완료`);
      } catch (error) {
        console.error('마커 생성 중 오류:', error);
      }
    }
  }, [map, parkingData]);

  // 주차장 항목 클릭 핸들러
  const handleParkingItemClick = (parking, index) => {
    if (map && markers && markers[index]) {
      const markerPosition = markers[index].getPosition();
      map.setCenter(markerPosition);
      map.setLevel(3);
      
      const infowindow = new window.kakao.maps.InfoWindow({
        content: `
          <div style="padding:10px; min-width:200px;">
            <div style="font-weight:bold; color:#333; margin-bottom:5px;">
              ${parking.PKLT_NM}
            </div>
            <div style="font-size:12px; color:#666; margin-bottom:5px;">
              ${parking.ADDR}
            </div>
            <div style="font-size:12px;">
              <span style="color:#007bff;">총 ${parking.TPKCT}대</span> | 
              <span style="color:#28a745;">현재 ${parking.NOW_PRK_VHCL_CNT}대</span>
            </div>
            <div style="font-size:11px; color:#999; margin-top:3px;">
              ${parking.PAY_YN_NM} | ${parking.PRK_TYPE_NM}
            </div>
          </div>
        `
      });
      
      infowindow.open(map, markers[index]);
      
      setTimeout(() => {
        infowindow.close();
      }, 3000);
      
      console.log(`지도 이동: ${parking.PKLT_NM}`);
    }
  };

  // 검색 핸들러
  const handleSearch = () => {
    console.log('검색어:', searchQuery);
  };

  if (loading) {
    return (
      <div className="loading">
        주차장 정보를 불러오는 중...
        <br />
        <small>데이터 개수: {parkingData ? parkingData.length : 0}</small>
      </div>
    );
  }

  return (
    <div className="App">
      {/* 상단 네비게이션 */}
      <header className="header">
        <div className="logo">
          <img src="/images/jari.png" alt="자리있어요 로고" className="logo-image" />
          자리있어요?
        </div>
        <nav className="nav-menu">
          <a href="#" className="nav-item">노외 주차장 찾기</a>
          <a href="#" className="nav-item">노상 주차장 찾기</a>
          <a href="#" className="nav-item">길찾기</a>
          <a href="#" className="nav-item active">주차자리 둘러보기</a>
        </nav>
        <div className="user-menu">로그인 / 회원가입</div>
      </header>

      {/* 메인 컨테이너 */}
      <div className="main-container">
        {/* 왼쪽 사이드바 */}
        <aside className="sidebar">
          {/* 검색 영역 */}
          <div className="search-section">
            <h2 className="search-title">어디 자리 있어요?</h2>
            <div className="search-box">
              <input 
                type="text" 
                className="search-input" 
                placeholder="구를 검색해주세요 ex)강남구"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              />
              <button className="search-btn" onClick={handleSearch}>🔍</button>
            </div>
          </div>

          {/* 주차장 목록 */}
          <div className="parking-list">
            {parkingData && parkingData.length > 0 ? (
              parkingData.map((parking, index) => {
                // console.log('렌더링할 주차장 데이터:', parking);
                return (
                  <div 
                    key={parking.PKLT_CD || index} 
                    className="parking-item"
                    onClick={() => handleParkingItemClick(parking, index)}
                  >
                    <div className="parking-name">
                      {parking.PKLT_NM || '주차장 이름 없음'}
                    </div>
                    <div className="parking-address">
                      {parking.ADDR || '주소 정보 없음'}
                    </div>
                    <div className="parking-info">
                      <span className="parking-capacity">
                        총 주차 면수 {parking.TPKCT || 0}대
                      </span>
                      <span className="parking-status">
                        현재 주차대수 {parking.NOW_PRK_VHCL_CNT || 0}대
                      </span>
                    </div>
                    <div className="parking-extra">
                      <small>
                        {parking.PAY_YN_NM || ''} | {parking.PRK_TYPE_NM || ''}
                      </small>
                    </div>
                  </div>
                );
              })
            ) : (
              <div className="no-data">
                주차장 데이터: {parkingData ? parkingData.length : 0}개
                <br />
                데이터 로딩 중...
              </div>
            )}
          </div>
        </aside>

        {/* 지도 영역 */}
        <main className="map-container">
          {mapError ? (
            <div style={{
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'center',
              alignItems: 'center',
              height: '100%',
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white',
              textAlign: 'center',
              fontFamily: 'Arial, sans-serif'
            }}>
              <div style={{ fontSize: '48px', marginBottom: '20px' }}>🗺️</div>
              <div style={{ fontSize: '24px', fontWeight: 'bold', marginBottom: '10px' }}>지도 서비스 오류</div>
              <div style={{ fontSize: '16px', opacity: 0.8 }}>서울시 주차장 위치 안내</div>
              <div style={{ fontSize: '14px', marginTop: '20px', opacity: 0.6 }}>
                카카오맵 로드 실패<br/>
                왼쪽 목록에서 주차장을 선택하세요<br/>
                <small>{mapError.message}</small><br/>
                <button 
                  onClick={() => window.location.reload()} 
                  style={{
                    marginTop: '10px',
                    padding: '8px 16px',
                    background: 'rgba(255,255,255,0.2)',
                    border: '1px solid rgba(255,255,255,0.3)',
                    borderRadius: '4px',
                    color: 'white',
                    cursor: 'pointer'
                  }}
                >
                  새로고침
                </button>
              </div>
            </div>
          ) : isMapLoading ? (
            <div style={{
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'center',
              alignItems: 'center',
              height: '100%',
              background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
              color: 'white',
              textAlign: 'center',
              fontFamily: 'Arial, sans-serif'
            }}>
              <div style={{ fontSize: '48px', marginBottom: '20px', animation: 'pulse 2s infinite' }}>🗺️</div>
              <div style={{ fontSize: '24px', fontWeight: 'bold', marginBottom: '10px' }}>카카오맵 로딩 중...</div>
              <div style={{ fontSize: '16px', opacity: 0.8 }}>서울시 주차장 지도를 준비하고 있습니다</div>
              <div style={{ fontSize: '14px', marginTop: '20px', opacity: 0.6 }}>
                잠시만 기다려주세요...
              </div>
            </div>
          ) : (
            <div id="map"></div>
          )}
        </main>
      </div>
    </div>
  );
}

export default App;
